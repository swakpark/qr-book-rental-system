package com.example.library.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String accessToken = extractCookie(request, "ACCESS_TOKEN");

        // 1. Access 유효한 경우
        if (accessToken != null && jwtProvider.validateToken(accessToken)) {
            setAuthentication(accessToken);
        }

        // 2. Access 만료 or 없음 -> Refresh로 자동 재발급 시도
        else {
            String refreshToken = extractCookie(request, "REFRESH_TOKEN");

            if (refreshToken != null && jwtProvider.validateToken(refreshToken)) {

                String username = jwtProvider.getUsername(refreshToken);
                String stored = refreshTokenService.get(username);

                if (stored != null && stored.equals(refreshToken)) {

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    String role = userDetails.getAuthorities().iterator().next().getAuthority();

                    // 새로운 Access 발급
                    String newAccess = jwtProvider.createToken(username, role);

                    Cookie newAccessCookie = new Cookie("ACCESS_TOKEN", newAccess);

                    newAccessCookie.setHttpOnly(true);
                    newAccessCookie.setPath("/");
                    newAccessCookie.setMaxAge(60 * 30);

                    response.addCookie(newAccessCookie);

                    // SecurityContext 재설정
                    setAuthentication(newAccess);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String token) {

        String username = jwtProvider.getUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String extractCookie(HttpServletRequest request, String name) {

        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}