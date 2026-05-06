// 1. Tạo class Provider để ánh xạ Claim "userId"
using Microsoft.AspNetCore.SignalR;

public class CustomUserIdProvider : IUserIdProvider
{
    public string GetUserId(HubConnectionContext connection)
    {
        // Trích xuất claim có tên là "userId" thay vì dùng Subject mặc định
        return connection.User?.FindFirst("userId").Value;
    }
}