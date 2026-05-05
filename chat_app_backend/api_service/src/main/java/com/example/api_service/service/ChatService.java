package com.example.api_service.service;

import java.util.Base64;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.api_service.dto.ChatPageResponse;
import com.example.api_service.repository.MessageRepository;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final MessageRepository chatRepository;
    public ChatPageResponse getChatHistory(Integer senderId, Integer receiverId, String cursor, int limit) {
        String messageId = (senderId < receiverId)
            ? senderId + "_" + receiverId
            : receiverId + "_" + senderId;
        Map<String, AttributeValue> lastKey = decodeCursor(messageId, cursor);
        MessageRepository.PagedMessages pagedResult = chatRepository.findMessageById(messageId, limit, lastKey);
        String nextCursor = encodeCursor(pagedResult.getLastEvaluatedKey());
        return new ChatPageResponse(
            pagedResult.getMessages(),
            nextCursor,
            nextCursor != null
        );
    }

    private String encodeCursor(Map<String, AttributeValue> lastKey) {
        if(lastKey == null || !lastKey.containsKey("ChatId")) return null;
        String chatId = lastKey.get("ChatId").s();
        return Base64.getEncoder().encodeToString(chatId.getBytes());
    }

    private Map<String, AttributeValue> decodeCursor(String messageId, String cursor) {
        if (cursor == null || cursor.isEmpty()) return null;
        String chatId = new String(Base64.getDecoder().decode(cursor));
        return Map.of(
            "MessageId", AttributeValue.builder().s(messageId).build(),
            "ChatId", AttributeValue.builder().s(chatId).build()
        );
    }
}
