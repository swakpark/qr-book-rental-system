package com.example.library.dto;

import com.example.library.model.Book;
import lombok.Getter;

@Getter
public class BookResponse {

    private Long id;
    private String title;
    private String author;
    private boolean available;

    public BookResponse(Long id, String title, String author, boolean available) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.available = available;
    }

    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.isAvailable()
        );
    }
}
