package com.example.library.controller.admin;

import com.example.library.model.Book;
import com.example.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/qr/print")
public class QrPrintController {

    private final BookRepository bookRepository;

    // 책 QR 인쇄
    @GetMapping("/books")
    public String printBooks(Model model) {
        List<Book> books = bookRepository.findAll();
        model.addAttribute("books", books);
        return "admin/qr/print-books";
    }

    // 테이블 QR 인쇄
    @GetMapping("/tables")
    public String printTables(Model model) {
        model.addAttribute("tableCount", 10);
        return "admin/qr/print-tables";
    }
}
