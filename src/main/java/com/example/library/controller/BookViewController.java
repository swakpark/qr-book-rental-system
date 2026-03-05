package com.example.library.controller;

import com.example.library.dto.ZoneBookResponse;
import com.example.library.model.Book;
import com.example.library.model.Zone;
import com.example.library.repository.BookRepository;
import com.example.library.repository.ZoneRepository;
import com.example.library.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/books")
public class BookViewController {

    private final BookService bookService;
    private final BookRepository bookRepository;
    private final ZoneRepository zoneRepository;

    public BookViewController(BookService bookService, BookRepository bookRepository, ZoneRepository zoneRepository) {
        this.bookService = bookService;
        this.bookRepository = bookRepository;
        this.zoneRepository = zoneRepository;
    }

    @GetMapping("/search-page")
    public String searchPage() {
        return "books/search-page";
    }

    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        List<Book> books = bookService.searchByTitle(keyword);

        if (books.isEmpty()) {
            model.addAttribute("message", "검색 결과가 없습니다.");
        } else {
            model.addAttribute("books", books);
        }

        return "books/search-page";
    }

    @GetMapping("/{id}")
    public String bookDetail(@PathVariable Long id, Model model) {

        Book book = bookService.getBook(id);
        Zone currentZone = book.getZone();

        List<Zone> zonesFloor1 = zoneRepository.findByFloor(1);
        List<Zone> zonesFloor2 = zoneRepository.findByFloor(2);

        int anchorX;
        int anchorY;
        Zone stairs;

        if (currentZone.getFloor() == 1) {
            anchorX = 24;
            anchorY = 18;
            stairs = zoneRepository.findByCodeAndFloor("STAIRS", 1).orElse(null);
        } else {
            anchorX = 24;
            anchorY = 18;
            stairs = zoneRepository.findByCodeAndFloor("STAIRS", 2).orElse(null);
        }

        model.addAttribute("book", book);
        model.addAttribute("currentZone", currentZone);
        model.addAttribute("zonesFloor1", zonesFloor1);
        model.addAttribute("zonesFloor2", zonesFloor2);
        model.addAttribute("stairs", stairs);
        model.addAttribute("anchorX", anchorX);
        model.addAttribute("anchorY", anchorY);
        model.addAttribute("shelfCounts", bookService.getShelfBookCounts());

        return "books/book-detail";
    }

    @GetMapping("/zones/{zoneId}")
    @ResponseBody
    public List<ZoneBookResponse> getBookByZone(@PathVariable Long zoneId) {

        return bookRepository.findByZoneId(zoneId)
                .stream()
                .map(book -> new ZoneBookResponse(
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getImage(),
                        book.isAvailable(),
                        book.getShelf() != null ? book.getShelf().getCode() : "미지정",
                        book.getShelfLevel()
                ))
                .toList();
    }
}