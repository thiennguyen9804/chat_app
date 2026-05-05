package com.example.api_service.script;

import com.github.f4b6a3.ulid.UlidCreator;
import com.example.api_service.entity.ChatMessage; // Thay bằng package thực tế của bạn
import org.springframework.boot.CommandLineRunner;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;

public class ChatDataSeeder implements CommandLineRunner {

    private final DynamoDbTable<ChatMessage> messageTable;

    public ChatDataSeeder(DynamoDbEnhancedClient enhancedClient) {
        // Khởi tạo table object
        this.messageTable = enhancedClient.table("Messages", TableSchema.fromBean(ChatMessage.class));
    }

    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra xem dữ liệu đã có chưa để tránh seed trùng lặp mỗi khi restart app (Tùy chọn)
        // Nếu bạn muốn mỗi lần chạy đều seed thêm thì bỏ qua bước kiểm tra.

        String messageId = "2_101";
        List<Integer> userIds = List.of(2, 101);

        System.out.println(">>> Bắt đầu seeding 20 tin nhắn cho: " + messageId);

        for (int i = 0; i < 20; i++) {
            // Tạo entity
            ChatMessage msg = new ChatMessage();
            msg.setMessageId(messageId);
            
            // Sinh ULID (Tự động sắp xếp theo thời gian)
            msg.setChatId(UlidCreator.getUlid().toString());
            
            // Luân phiên người gửi: i=0 (user 2), i=1 (user 101), i=2 (user 2)...
            msg.setSenderId(userIds.get(i % 2));
            
            msg.setContent("Nội dung tin nhắn mẫu thứ " + (i + 1));

            // Lưu vào DynamoDB
            messageTable.putItem(msg);

            // Nghỉ 10ms để các ULID có khoảng cách thời gian rõ ràng
            Thread.sleep(10);
            
            if ((i + 1) % 5 == 0) {
                System.out.println("Đã seed được " + (i + 1) + " tin nhắn...");
            }
        }

        System.out.println(">>> Hoàn tất seeding dữ liệu!");
    }
}