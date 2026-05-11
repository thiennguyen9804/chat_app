package com.example.chat_app_backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.api_service.ApiServiceApplication;
import com.example.api_service.dto.AuthResponse;
import com.example.api_service.dto.LoginRequest;
import com.example.api_service.service.AuthService;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = ApiServiceApplication.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private DiscoveryClient discoveryClient;

    @Test
    void shouldReturnAsiaServer_WhenUserIsFromAsia() throws Exception {
        // Given
        String username = "testuser";
        String password = "password";
        String region = "Asia";
        String asiaUrl = "http://asia-server:8080";
        String europeUrl = "http://europe-server:8080";

        var authResponse = new AuthResponse(1, "mock-jwt-token");
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        ServiceInstance asiaInstance = mock(ServiceInstance.class);
        when(asiaInstance.getMetadata()).thenReturn(Map.of("region", "Asia"));
        when(asiaInstance.getUri()).thenReturn(URI.create(asiaUrl));

        ServiceInstance europeInstance = mock(ServiceInstance.class);
        when(europeInstance.getMetadata()).thenReturn(Map.of("region", "EUROPE"));
        when(europeInstance.getUri()).thenReturn(URI.create(europeUrl));

        when(discoveryClient.getInstances("chat-service-api")).thenReturn(List.of(asiaInstance, europeInstance));

        String loginRequestJson = """
        {
            "username": "%s",
            "password": "%s",
            "region": "%s"
        }
        """.formatted(username, password, region);

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.accessToken").value("mock-jwt-token"))
                .andExpect(jsonPath("$.chatServerUrlString").value(asiaUrl));
    }
}
