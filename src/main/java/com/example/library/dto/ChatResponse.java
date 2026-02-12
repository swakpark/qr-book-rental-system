package com.example.library.dto;

public class ChatResponse {

    private String reply;

    public ChatResponse(String reply) {
        this.reply = reply;
    }

    public static ChatResponse from(String reply) {
        return new ChatResponse(reply);
    }

    public String getReply() {
        return reply;
    }
}
