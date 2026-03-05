package com.example.library.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateBookRequest {

    @NotBlank(message = "도서 제목은 필수입니다.")
    private String title;

    @NotBlank(message = "저자는 필수입니다.")
    private String author;

    @NotBlank(message = "ISBN은 필수입니다.")
    private String isbn;

    private String category;

    private String publisher;

    private String image;

}
