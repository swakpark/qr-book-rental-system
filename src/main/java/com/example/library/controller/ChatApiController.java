package com.example.library.controller;


import com.example.library.dto.ChatHistoryDto;
import com.example.library.dto.ChatRequest;
import com.example.library.dto.ChatResponse;
import com.example.library.model.User;
import com.example.library.security.AuthUtil;
import com.example.library.service.ChatService;
import com.example.library.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatApiController {

    private final ChatService chatService;
    private final UserService userService;

    @PostMapping
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {

        if (!AuthUtil.isLoggedIn()) {
            return ChatResponse.from("⚠️ 로그인 후 이용 가능한 서비스입니다.");
        }

        User user = userService.getUserByEmail(AuthUtil.getEmail());

        String reply = chatService.reply(user, request.getMessage());
        return ChatResponse.from(reply);
    }

    @GetMapping("/history")
    public List<ChatHistoryDto> history() {

        if (!AuthUtil.isLoggedIn()) {
            return List.of();
        }

        User user = userService.getUserByEmail(AuthUtil.getEmail());

        return chatService.getHistory(user).stream()
                .map(ChatHistoryDto::from)
                .toList();
    }

    @DeleteMapping("/history")
    public void clearHistory() {

        User user = userService.getUserByEmail(AuthUtil.getEmail());
        chatService.clearHistory(user);
    }
}
