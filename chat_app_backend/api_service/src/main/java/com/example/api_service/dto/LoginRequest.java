package com.example.api_service.dto;

import lombok.NonNull;

public record LoginRequest(
    @NonNull String username,
    @NonNull String password

) {
    
}
