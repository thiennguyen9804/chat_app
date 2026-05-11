package com.example.api_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoutingService {
    private final DiscoveryClient client;
    public String findBestChatServer(String userLocation) {
        List<ServiceInstance> instances = client.getInstances("chat-service-api");
        if (instances.isEmpty()) {
            throw new RuntimeException("Không tìm thấy chat-service nào đang hoạt động!");
        }

        Optional<ServiceInstance> bestInstance = instances.stream()
            .filter(instance -> {
                String region = instance.getMetadata().get("region");
                return userLocation.equalsIgnoreCase(region);
            })
            .findFirst();
        return bestInstance
            .map(instance -> instance.getUri().toString())
            .orElse(instances.getFirst().getUri().toString());
    }
}
