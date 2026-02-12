package com.example.library.dto.naver;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NaverBookItem {

    private String title; // <b>태그 포함
    private String author;
    private String publisher;
    private String isbn; // "ISBN10 ISBN13
    private String image;
    private String description;
}
