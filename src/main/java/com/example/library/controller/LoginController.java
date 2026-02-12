package com.example.library.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    // 로그인 화면
    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String redirect, HttpSession session) {
        // redirect 값이 있으면 세션에 잠시 저장
        if (redirect != null && !redirect.isBlank()) {
            session.setAttribute("LOGIN_REDIRECT", redirect);
        }

        return "login"; // templates/login.html
    }
}
