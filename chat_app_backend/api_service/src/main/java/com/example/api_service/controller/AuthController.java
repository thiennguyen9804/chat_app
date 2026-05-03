package com.example.api_service.controller;


import org.springframework.web.bind.annotation.RestController;

import com.example.api_service.dto.AuthResponse;
import com.example.api_service.dto.LoginRequest;
import com.example.api_service.service.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService userService;
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest dto) {
        return userService.login(dto);
    }
}
