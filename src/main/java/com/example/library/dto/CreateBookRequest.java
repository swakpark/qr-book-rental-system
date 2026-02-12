package com.example.library.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateBookRequest {

    @NotBlank(message = "도서 제목은 필수입니다.")
    private String title;

    @NotBlank(message = "저자는 필수입니다.")
    private String author;

    @NotBlank(message = "ISBN은 필수입니다.")
    private String isbn;

    private String publisher;

    private String image;

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getImage() {
        return image;
    }
}
