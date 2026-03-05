package com.example.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    // 로그인 화면
    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String redirect, Model model) {

        if (redirect != null && !redirect.isBlank()) {
            model.addAttribute("LOGIN_REDIRECT", redirect);
        }

        return "login"; // templates/login.html
    }
}
