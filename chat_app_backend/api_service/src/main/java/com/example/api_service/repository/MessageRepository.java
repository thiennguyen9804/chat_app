package com.example.api_service.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.example.api_service.entity.ChatMessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Repository
public class MessageRepository {
    private final DynamoDbTable<ChatMessage> table;
    public MessageRepository(DynamoDbEnhancedClient enhancedClient) {
        this.table = enhancedClient.table("Messages", TableSchema.fromBean(ChatMessage.class));
    }

    public PagedMessages findMessageById(String messageId, int limit, Map<String, AttributeValue> lastKey) {
        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
            .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(messageId)))
            .limit(limit)
            .scanIndexForward(false)
            .exclusiveStartKey(lastKey)
            .build();
        Page<ChatMessage> page = table.query(request).iterator().next();
        List<ChatMessage> messages = new ArrayList<>(page.items());
        Collections.reverse(messages);
        return new PagedMessages(messages, page.lastEvaluatedKey());
    }

    @Data
    @AllArgsConstructor
    public class PagedMessages {
        private List<ChatMessage> messages;
        private Map<String, AttributeValue> lastEvaluatedKey;
    }
}
