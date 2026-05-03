package com.example.api_service.controller;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController()
@RequestMapping("/hello")
@RequiredArgsConstructor
public class HelloController {
    @GetMapping
    public String sayHello() {
        var username = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new String("Hello" + username);
    }
}
