package com.example.library.dto;

import com.example.library.model.ChatHistory;

public record ChatHistoryDto(
        String role,   // "user" | "bot"
        String message
) {
    public static ChatHistoryDto from(ChatHistory history) {
        return new ChatHistoryDto(
                history.getRole().name().toLowerCase(),
                history.getMessage()
        );
    }
}
