package com.example.library.controller.qr;

import com.example.library.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/qr/books")
public class QrBookResultController {

    private final BookService bookService;

    public QrBookResultController(BookService bookService) {
        this.bookService = bookService;
    }

    // 대여 완료 화면
    @GetMapping("/{id}/loan/result")
    public String loanResult(@PathVariable Long id, Model model) {
        model.addAttribute("book", bookService.getBook(id));
        model.addAttribute("message", "📘 대여 완료");
        model.addAttribute("autoRedirect", true);

        return "qr/qr-result";
    }

    // 반납 완료 화면
    @GetMapping("/{id}/return/result")
    public String returnResult(@PathVariable Long id, Model model) {
        model.addAttribute("book", bookService.getBook(id));
        model.addAttribute("message", "📕 반납 완료");
        model.addAttribute("autoRedirect", true);

        return "qr/qr-result";
    }
}