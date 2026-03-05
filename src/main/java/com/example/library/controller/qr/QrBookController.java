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
    @PostMapping("/{bookId}/{signature}/loan")
    public String loanBook(@PathVariable Long bookId, @PathVariable String signature) {

        if (!AuthUtil.isLoggedIn()) {
            return "redirect:/login?redirect=/qr/books/" + bookId + "/" + signature;
        }

        User user = userService.getUserByEmail(AuthUtil.getEmail());
        loanService.loanBook(user, bookId);

        return "redirect:/qr/books/" + bookId + "/" + signature + "/loan/result";
    }

    // 도서 반납
    @PostMapping("/{bookId}/{signature}/return")
    public String returnBook(@PathVariable Long bookId, @PathVariable String signature) {

        if (!AuthUtil.isLoggedIn()) {
            return "redirect:/login?redirect=/qr/books/" + bookId + "/" + signature;
        }

        User user = userService.getUserByEmail(AuthUtil.getEmail());
        loanService.returnBook(user, bookId);

        return "redirect:/qr/books/" + bookId + "/" + signature + "/return/result";
    }
}

