package com.example.api_service.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.api_service.dto.AuthResponse;
import com.example.api_service.dto.LoginRequest;
import com.example.api_service.entity.AuthUser;
import com.example.api_service.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtTokenService;
    public AuthResponse login(LoginRequest dto) {
        var token = new UsernamePasswordAuthenticationToken(dto.username(), dto.password());
        Authentication authentication = authenticationManager.authenticate(token);
        var userId = ((AuthUser) authentication.getPrincipal()).getId();
        String jwtToken = jwtTokenService.generateToken(userId, dto.username());
        return new AuthResponse(userId, jwtToken);
    }
}
