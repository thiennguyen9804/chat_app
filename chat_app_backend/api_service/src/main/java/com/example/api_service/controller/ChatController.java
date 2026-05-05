package com.example.api_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.api_service.dto.ChatPageResponse;
import com.example.api_service.service.ChatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    @GetMapping("/history")
    public ResponseEntity<ChatPageResponse> getChatHistory(
        @RequestParam("user1Id") Integer user1Id,
        @RequestParam("user2Id") Integer user2Id,
        @RequestParam(required = false, name = "cursor") String cursor,
        @RequestParam(defaultValue = "20", name="limit") int limit
    ) {
        ChatPageResponse response = chatService.getChatHistory(user1Id, user2Id, cursor, limit);
        return ResponseEntity.ok(response);
    }
}
