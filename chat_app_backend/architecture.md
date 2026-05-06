# Architecture Document

## 1. Giới thiệu

Hệ thống là một ứng dụng **chat 1-1 realtime** được xây dựng bằng Spring Boot.  
Thiết kế tuân thủ theo tinh thần của ByteByteGo "Design a Chat System", tập trung vào tính scalable và tách biệt rõ ràng giữa stateless và stateful layers.

**Phiên bản hiện tại**: MVP (chỉ hỗ trợ chat 1-1, chưa có group chat và multi-device sync đầy đủ).

## 2. Tech Stack

| Layer                  | Công nghệ                          |
|------------------------|------------------------------------|
| Backend Framework      | **Spring Boot 3** + Java 21 + C# 12 + **ASP.NET 8** +        |
| WebSocket              | Spring WebSocket + STOMP           |
| Database (Messages)    | **Amazon DynamoDB**                |
| Message Queue          | **RabbitMQ**                       |
| Service Discovery      | **Apache ZooKeeper**               |
| Authentication         | JWT (Spring Security + JJWT)       |
| ORM / Data Access      | Suitable DynamoDB ORM for each framework             |
| Build Tool             | Gradle (for Spring) + NuGet (for ASP.NET)                     |
| Container              | Docker + Docker Compose            |

## 3. High-Level Architecture

Hệ thống được chia thành hai phần chính:

- **API Server** (Stateless): Xử lý HTTP REST API
- **Chat Server** (Stateful): Xử lý WebSocket realtime

### Các thành phần:

- **Client** (Mobile/Web): Gọi HTTP đến API Server và kết nối WebSocket đến Chat Server
- **API Server**: Xử lý Auth, User, Conversation List, Load Message History
- **Chat Server**: Quản lý WebSocket connection, gửi/nhận tin nhắn realtime, heartbeat
- **ZooKeeper**: Service Registry & Discovery cho các Chat Server
- **DynamoDB**: Lưu trữ lịch sử tin nhắn (messages) và thông tin user
- **RabbitMQ**: 
  - Fanout / routing tin nhắn giữa các Chat Server
  - Xử lý async tasks (ví dụ: lưu message, gửi push notification sau này)

## 4. Data Flow chính

### 4.1 Login Flow
1. Client → **API Server**: `POST /api/auth/login`
2. API Server lấy danh sách Chat Server healthy từ **ZooKeeper**
3. API Server chọn một Chat Server (round-robin hoặc random)
4. Trả về `access_token` + `ws_url`
5. Client kết nối WebSocket đến Chat Server và gửi lệnh AUTH

### 4.2 Send Message Flow
1. Client → Chat Server: gửi tin nhắn qua WebSocket
2. Chat Server publish message vào **RabbitMQ**
3. Consumer lắng nghe RabbitMQ → lưu message vào **DynamoDB**
4. Chat Server đẩy `new_message` realtime đến người nhận (nếu đang online)
5. Gửi back `message_delivered` cho người gửi

### 4.3 Load Messages Flow
1. Client → API Server: `GET /api/chats/{chat_id}/messages?limit=40&before=xxx`
2. API Server query trực tiếp từ **DynamoDB**
3. Trả về danh sách tin nhắn có phân trang (cursor-based)

## 5. Database Schema (DynamoDB)

### 5.1 Table: `Users`

- **Partition Key**: `userId` (String)
- Các thuộc tính:
  - `username`
  - `email`
  - `passwordHash`
  - `name`
  - `avatarUrl`
  - `lastActiveAt`
  - `createdAt`

### 5.2 Table: `Messages` (Quan trọng nhất)

- **Partition Key**: `chatId` (String) → Ví dụ: `chat_100_200`
- **Sort Key**: `messageId` (String) → Snowflake ID hoặc ULID (sortable theo thời gian)
- Các thuộc tính khác:
  - `senderId`
  - `content`
  - `messageType` (text, image, file, ...)
  - `replyToMessageId`
  - `createdAt`
  - `metadata` (JSON String)

**Global Secondary Index (GSI)**:
- `senderId-createdAt-index` (để query tin nhắn theo người gửi nếu cần)

### 5.3 Table: `UserChatMemberships`

- **Partition Key**: `userId`
- **Sort Key**: `chatId`
- Thuộc tính: `partnerId`, `unreadCount`, `lastReadMessageId`, `joinedAt`

## 6. API Endpoints (API Server)

### Authentication
- `POST /api/auth/login`
- `POST /api/auth/refresh-token`

### User
- `GET /api/users/me`
- `GET /api/users/{userId}`

### Chat
- `GET /api/chats` — Lấy danh sách cuộc trò chuyện
- `GET /api/chats/{chatId}/messages` — Lấy lịch sử tin nhắn (`limit`, `before`)

## 7. WebSocket Events (Chat Server)

**Client → Server:**
- `auth`
- `send_message`
- `heartbeat`
- `message_ack`

**Server → Client:**
- `auth_success`
- `new_message`
- `message_delivered`
- `presence_update`

## 8. Vai trò của RabbitMQ

- Giúp tách biệt việc nhận tin nhắn realtime và lưu trữ vào DynamoDB
- Hỗ trợ fanout tin nhắn khi sau này mở rộng group chat
- Xử lý retry và dead letter queue khi lưu message thất bại

## 9. Roadmap

**Phase 1 (MVP)**: Login + Chat 1-1 realtime + Load history (đang thực hiện)  
**Phase 2**: Presence + Delivery & Read Receipt  
**Phase 3**: Attachment, Multi-device, Group Chat

---

**Cập nhật**: 01/05/2026  
**Tác giả**: [Tên của bạn]
