package com.example.library.controller.admin;

import com.example.library.model.Book;
import com.example.library.model.Loan;
import com.example.library.service.BookService;
import com.example.library.service.LoanService;
import com.example.library.service.qr.QrTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/admin/view")
@RequiredArgsConstructor
public class AdminBookViewController {

    private final BookService bookService;
    private final LoanService loanService;
    private final QrTokenService qrTokenService;

    @GetMapping("/{id}")
    public String bookPage(@PathVariable Long id, Model model) {

        Book book = bookService.getBook(id);
        model.addAttribute("book", book);

        // 현재 대여 정보 조회
        Optional<Loan> activeLoan = loanService.getActiveLoan(book);
        model.addAttribute("loan", activeLoan.orElse(null));

        String token = qrTokenService.generateBookToken(id);

        // QR 코드 URL
        model.addAttribute("qrUrl",
                "https://rylie-crunchier-paul.ngrok-free.dev/qr/books/" + id + "?token=" + token);

        return "admin/book-information"; // templates/book-information.html
    }
}
