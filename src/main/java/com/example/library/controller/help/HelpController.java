package com.example.library.controller.help;

import com.example.library.model.User;
import com.example.library.security.AuthUtil;
import com.example.library.service.LoanService;
import com.example.library.service.UserService;
import com.example.library.service.policy.OverduePolicy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/help")
public class HelpController {

    private final LoanService loanService;
    private final UserService userService;

    public HelpController(LoanService loanService, UserService userService) {
        this.loanService = loanService;
        this.userService = userService;
    }

    @GetMapping
    public String helpHome(Model model) {
        if (!AuthUtil.isLoggedIn()) {
            return "redirect:/login?redirect=/help";
        }

        User user = userService.getUserByEmail(AuthUtil.getEmail());
        OverduePolicy policy = loanService.evaluateOverduePolicy(user);

        model.addAttribute("overduePolicy", policy);
        return "help/index";
    }

    @PostMapping
    public String submitHelp(@RequestParam String message, Model model) {
        if (!AuthUtil.isLoggedIn()) {
            return "redirect:/login?redirect=/help";
        }

        User user = userService.getUserByEmail(AuthUtil.getEmail());

        // TODO: 나중에 DB 저장 or 관리자 알림
        System.out.println("문의 from " + user.getEmail());
        System.out.println(message);

        model.addAttribute("message", "문의가 접수되었습니다.");
        model.addAttribute("autoRedirect", false);

        return "qr/qr-result";
    }
}
