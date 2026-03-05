package com.example.library.dto;

public record ZoneBookResponse(
        Long id,
        String title,
        String author,
        String image,
        boolean available,
        String shelfCode,
        int shelfLevel
) {}
