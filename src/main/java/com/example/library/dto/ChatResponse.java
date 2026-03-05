package com.example.library.dto;

import lombok.Getter;

@Getter
public class ChatResponse {

    private String reply;

    public ChatResponse(String reply) {
        this.reply = reply;
    }

    public static ChatResponse from(String reply) {
        return new ChatResponse(reply);
    }

}
