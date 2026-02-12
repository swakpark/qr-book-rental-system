package com.example.library.controller;

import com.example.library.service.CustomUserDetailsService;
import com.example.library.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;


@Controller
public class RegisterController {

    private final UserService userService;
    private final CustomUserDetailsService userDetailsService;

    public RegisterController(UserService userService, CustomUserDetailsService userDetailsService) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
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
            HttpServletRequest request
    ) {
        // 회원가입
        userService.register(name, email, password);

        // 자동 로그인 처리
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        // SecurityContext에 설정
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // 세션에 저장
        request.getSession(true)
                .setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                        context
                );

        // redirect 복귀
        if (redirect != null && !redirect.isBlank()) {
            return "redirect:" + redirect;
        }

        return "redirect:/qr/entry";
    }
}