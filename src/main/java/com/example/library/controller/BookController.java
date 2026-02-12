package com.example.library.controller;

import com.example.library.dto.ApiResponse;
import com.example.library.dto.BookResponse;
import com.example.library.dto.CreateBookRequest;
import com.example.library.model.Book;
import com.example.library.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookResponse>> addBook(@RequestBody @Valid CreateBookRequest request) {
        Book book = bookService.addBook(request);
        return ResponseEntity.ok(ApiResponse.success(BookResponse.from(book)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> getBook(@PathVariable Long id) {
        Book book = bookService.getBook(id);
        return ResponseEntity.ok(ApiResponse.success(BookResponse.from(book)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookResponse>>> getAllBooks() {
        List<BookResponse> responses = bookService.getAllBooks()
                .stream()
                .map(BookResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}
