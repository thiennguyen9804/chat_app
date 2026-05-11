package com.example.api_service.controller;


import org.springframework.web.bind.annotation.RestController;

import com.example.api_service.dto.AuthResponse;
import com.example.api_service.dto.LoginRequest;
import com.example.api_service.service.AuthService;
import com.example.api_service.service.ChatRoutingService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService userService;
    private final ChatRoutingService chatRoutingService;
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest dto) {

        var _authResponse = userService.login(dto);
        var chatServer = chatRoutingService.findBestChatServer(dto.region());
        var authResponse = new AuthResponse(
            _authResponse.userId(),
            _authResponse.accessToken(),
            chatServer
        );
        return authResponse;
    }
}
