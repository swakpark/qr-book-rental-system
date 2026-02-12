package com.example.library.controller.admin;

import com.example.library.dto.naver.NaverBookItem;
import com.example.library.service.BookService;
import com.example.library.service.naver.NaverBookImportService;
import com.example.library.service.naver.NaverBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import java.util.List;

@Controller
@RequestMapping("/admin/books")
@RequiredArgsConstructor
public class AdminBookController {

    private final NaverBookService naverBookService;
    private final NaverBookImportService naverBookImportService;
    private final BookService bookService;


    @GetMapping
    public String books(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "admin/books";
    }

    // 네이버 도서 검색 화면
    @GetMapping("/naver")
    public String searchForm() {
        return "admin/book-search";
    }

    // 검색 결과
    @GetMapping("/naver/search")
    public String search(@RequestParam String q, Model model) {
        List<NaverBookItem> items = naverBookService.search(q);
        model.addAttribute("items", items);
        model.addAttribute("q", q);
        return "admin/book-search";
    }

    // 도서 등록
    @PostMapping("/naver/import")
    public String importBook(@RequestParam String isbn) {
        NaverBookItem item = naverBookService.search(isbn).get(0);
        naverBookImportService.importFromNaver(item);
        return "redirect:/admin/books/naver";
    }
}
