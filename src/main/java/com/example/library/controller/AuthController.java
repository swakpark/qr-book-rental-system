package com.example.library.controller;

import com.example.library.security.JwtProvider;
import com.example.library.security.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsService userDetailsService;

    @PostMapping("/reissue")
    public void reissue(
            @CookieValue(value = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response
    ) throws Exception {

        // 1. refresh 존재 + 유효성 검사
        if (refreshToken == null || !jwtProvider.validateToken(refreshToken)) {
            clearCookies(response);
            response.sendRedirect("/login?expired");
            return;
        }

        // 2. refresh에서 username 추출
        String username = jwtProvider.getUsername(refreshToken);

        // 3. Redis 저장값과 비교
        String stored = refreshTokenService.get(username);

        // 4. Redis mismatch
        if (stored == null || !stored.equals(refreshToken)) {
            // 의심 상황이면 Redis, 쿠키 삭제
            refreshTokenService.delete(username);
            clearCookies(response);
            response.sendRedirect("/login?expired");
            return;
        }

        // 5. role은 하드코딩하지 않고 사용자 권한에서 가져오기
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        // 6. 새로운 토큰 발급 (Rotation)
        String newAccess = jwtProvider.createToken(username, role);
        String newRefresh = jwtProvider.createRefreshToken(username);

        // 7. Redis 교체 (기존 refresh 제거)
        long refreshTtlMillis = jwtProvider.getRefreshExpiration();
        refreshTokenService.save(username, newRefresh, refreshTtlMillis);

        // 8. 쿠키 갱신
        Cookie accessCookie = new Cookie("ACCESS_TOKEN", newAccess);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(60 * 30);

        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", newRefresh);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge((int) (refreshTtlMillis / 1000));

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }

    private void clearCookies(HttpServletResponse response) {

        Cookie access = new Cookie("ACCESS_TOKEN", null);
        access.setHttpOnly(true);
        access.setPath("/");
        access.setMaxAge(0);

        Cookie refresh = new Cookie("REFRESH_TOKEN", null);
        refresh.setHttpOnly(true);
        refresh.setPath("/");
        refresh.setMaxAge(0);

        response.addCookie(access);
        response.addCookie(refresh);
    }
}
