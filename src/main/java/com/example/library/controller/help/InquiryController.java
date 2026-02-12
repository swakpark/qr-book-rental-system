package com.example.library.controller.help;

import com.example.library.model.User;
import com.example.library.security.AuthUtil;
import com.example.library.service.InquiryService;
import com.example.library.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/help/inquiry")
public class InquiryController {

    private final InquiryService inquiryService;
    private final UserService userService;

    public InquiryController(InquiryService inquiryService, UserService userService) {
        this.inquiryService = inquiryService;
        this.userService = userService;
    }

    @PostMapping
    public String submit(@RequestParam String message) {
        if (!AuthUtil.isLoggedIn()) {
            return "redirect:/login?redirect=/help/inquiry";
        }

        User user = userService.getUserByEmail(AuthUtil.getEmail());
        inquiryService.createInquiry(user, message);

        return "help/inquiry-result";
    }
}
