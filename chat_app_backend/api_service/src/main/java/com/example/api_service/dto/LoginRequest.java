package com.example.api_service.dto;

import jakarta.annotation.Nullable;
import lombok.NonNull;

public record LoginRequest(
    @NonNull String username,
    @NonNull String password,
    @Nullable String region
) {
    
}
