package com.example.api_service.repository;


import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.api_service.entity.AuthUser;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Repository
public class AuthUserRepository {
    private final DynamoDbTable<AuthUser> userTable;
    public AuthUserRepository(DynamoDbEnhancedClient enhancedClient) {
        // Map Class AuthUser với bảng "Users" trong DB
        this.userTable = enhancedClient.table("Users", TableSchema.fromBean(AuthUser.class));
    }
    public void save(AuthUser user) {
        userTable.putItem(user);
    }

    public AuthUser findById(Integer id) {
        return userTable.getItem(Key.builder().partitionValue(id).build());
    }

    public Optional<AuthUser> findByUsername(String username) {
        // 1. Tạo điều kiện lọc (Filter Expression)
        Expression filterExpression = Expression.builder()
                .expression("#u = :val")
                .putExpressionName("#u", "Username") // Khớp với @DynamoDbAttribute("Username")
                .putExpressionValue(":val", AttributeValue.builder().s(username).build())
                .build();

        // 2. Thực hiện quét bảng
        ScanEnhancedRequest request = ScanEnhancedRequest.builder()
                .filterExpression(filterExpression)
                .build();

        // 3. Lấy kết quả đầu tiên tìm thấy
        return userTable.scan(request)
            .items()
            .stream()
            .findFirst();
    }
}