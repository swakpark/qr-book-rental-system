package com.example.library.controller;

import com.example.library.security.RefreshTokenService;
import com.example.library.service.CustomUserDetailsService;
import com.example.library.service.UserService;
import com.example.library.security.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;


@Controller
public class RegisterController {

    private final UserService userService;
    private final CustomUserDetailsService userDetailsService;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    public RegisterController(UserService userService, CustomUserDetailsService userDetailsService,
                              JwtProvider jwtProvider, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.jwtProvider = jwtProvider;
        this.refreshTokenService = refreshTokenService;
    }

    // 회원가입 화면
    @GetMapping("/register")
    public String registerForm(
            @RequestParam(required = false) String redirect,
            Model model
    ) {
        model.addAttribute("redirect", redirect);
        return "register";
    }

    // 회원가입 처리
    @PostMapping("/register")
    public String register(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) String redirect,
            HttpServletResponse response
    ) {

        // 자동 로그인
        // 회원가입
        userService.register(name, email, password);

        // 사용자 정보 로드
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        String role = userDetails.getAuthorities()
                .iterator()
                .next()
                .getAuthority();

        // JWT 생성
        String accessToken = jwtProvider.createToken(email, role);
        String refreshToken = jwtProvider.createRefreshToken(email);

        // Redis에 Refresh 저장
        refreshTokenService.save(email, refreshToken, jwtProvider.getRefreshExpiration());

        // Access 쿠키
        Cookie accessCookie = new Cookie("ACCESS_TOKEN", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(60 * 30);

        // Refresh 쿠키
        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(60 * 60 * 24 * 7);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        // redirect 복귀
        if (redirect != null && !redirect.isBlank()) {
            return "redirect:" + redirect;
        }

        return "redirect:/qr/entry";
    }
}