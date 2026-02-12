package com.example.library.controller.qr;

import com.example.library.model.User;
import com.example.library.security.AuthUtil;
import com.example.library.service.LoanService;

import com.example.library.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/qr/books")
public class QrBookController {

    private final LoanService loanService;
    private final UserService userService;

    public QrBookController(LoanService loanService, UserService userService) {
        this.loanService = loanService;
        this.userService = userService;
    }

    // 도서 대여
    @PostMapping("/{id}/loan")
    public String loanBook(@PathVariable Long id, @RequestParam String token) {

        if (!AuthUtil.isLoggedIn()) {
            return "redirect:/login?redirect=/qr/books/" + id + "?token=" + token;
        }

        User user = userService.getUserByEmail(AuthUtil.getEmail());
        loanService.loanBook(user, id);

        return "redirect:/qr/books/" + id + "/loan/result?token=" + token;
    }

    // 도서 반납
    @PostMapping("/{id}/return")
    public String returnBook(@PathVariable Long id, @RequestParam String token) {

        if (!AuthUtil.isLoggedIn()) {
            return "redirect:/login?redirect=/qr/books/" + id + "?token=" + token;
        }

        User user = userService.getUserByEmail(AuthUtil.getEmail());
        loanService.returnBook(user, id);

        return "redirect:/qr/books/" + id + "/return/result?token=" + token;
    }
}

