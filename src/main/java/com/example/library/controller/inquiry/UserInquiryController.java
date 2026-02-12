package com.example.library.controller.inquiry;

import com.example.library.model.User;
import com.example.library.security.AuthUtil;
import com.example.library.service.InquiryService;
import com.example.library.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/inquiries")
public class UserInquiryController {

    private final InquiryService inquiryService;
    private final UserService userService;

    public UserInquiryController(InquiryService inquiryService, UserService userService) {
        this.inquiryService = inquiryService;
        this.userService = userService;
    }

    @GetMapping("/my")
    public String myInquiries(Model model) {

        if (!AuthUtil.isLoggedIn()) {
            return "redirect:/login?redirect=/inquiries/my";
        }
        User user = userService.getUserByEmail(AuthUtil.getEmail());

        inquiryService.markInquiriesAsChecked(user);
        model.addAttribute("inquiries", inquiryService.getUserInquiries(user));

        return "inquiry/my-inquiries";
    }
}
