package com.example.library.controller.qr;

import com.example.library.model.Book;
import com.example.library.model.Loan;
import com.example.library.model.User;
import com.example.library.qr.QrBookViewState;
import com.example.library.security.AuthUtil;
import com.example.library.service.BookService;
import com.example.library.service.LoanService;
import com.example.library.service.UserService;
import com.example.library.service.policy.OverduePolicy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/qr/books")
public class QrBookViewController {

    private final BookService bookService;
    private final LoanService loanService;
    private final UserService userService;

    public QrBookViewController(BookService bookService, LoanService loanService, UserService userService) {
        this.bookService = bookService;
        this.loanService = loanService;
        this.userService = userService;
    }

    // QR로 접속하는 도서 페이지
    @GetMapping("/{id}")
    public String qrBookPage(@PathVariable Long id, Model model) {

        // QR 접근 로그 남기기 (추가)
        log.info("[QR ACCESS] bookId={}, loggedIn={}", id, AuthUtil.isLoggedIn());

        Book book = bookService.getBook(id);
        model.addAttribute("book", book);

        boolean isLoggedIn = AuthUtil.isLoggedIn();
        model.addAttribute("isLoggedIn", isLoggedIn);

        if (isLoggedIn) {
            User user = userService.getUserByEmail(AuthUtil.getEmail());
            model.addAttribute("user", user);

            OverduePolicy overduePolicy = loanService.evaluateOverduePolicy(user);
            model.addAttribute("overduePolicy", overduePolicy);
        }

        Optional<Loan> activeLoanOpt = loanService.getActiveLoan(book);

        if (activeLoanOpt.isPresent()) {
            model.addAttribute("loan", activeLoanOpt.get());
            model.addAttribute("state", QrBookViewState.LOANED);
        } else {
            model.addAttribute("state", QrBookViewState.AVAILABLE);
        }

        return "qr/qr-book";
    }
}
