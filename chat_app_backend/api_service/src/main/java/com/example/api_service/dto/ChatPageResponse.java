package com.example.api_service.dto;

import java.util.List;

import com.example.api_service.entity.ChatMessage;

public record ChatPageResponse(
    List<ChatMessage> content,
    String nextCursor,
    boolean hasNext
) {}