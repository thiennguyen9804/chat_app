package com.example.api_service.entity;
import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
@Data
public class ChatMessage {

    private String messageId; // Partition Key (e.g., "2_101")
    private String chatId;    // Sort Key (ULID)
    private Integer senderId;
    private Integer receiverId;
    private String content;
    private String type = "TEXT";

    @DynamoDbPartitionKey
    @DynamoDbAttribute("MessageId")
    public String getMessageId() {
        return messageId;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("ChatId")
    public String getChatId() {
        return chatId;
    }

    @DynamoDbAttribute("SenderId")
    public Integer getSenderId() {
        return senderId;
    }

    @DynamoDbAttribute("Content")
    public String getContent() {
        return content;
    }

    @DynamoDbAttribute("ReceiverId")
    public Integer getReceiverId() {
        return receiverId;
    }

    @DynamoDbAttribute("Type")
    public String getType() {
        return type;
    }

}