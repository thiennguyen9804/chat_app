package com.example.api_service.dto;

import jakarta.annotation.Nonnull;

public record AuthResponse(
    @Nonnull Integer userId,
    @Nonnull String accessToken,
    @Nonnull String chatServerUrlString
) {
    public AuthResponse(Integer userId, String accessToken) {
        this(userId, accessToken, "");
    }
} 

