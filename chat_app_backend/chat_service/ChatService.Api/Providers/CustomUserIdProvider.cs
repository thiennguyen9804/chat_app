// 1. Tạo class Provider để ánh xạ Claim "userId"
using Microsoft.AspNetCore.SignalR;

public class CustomUserIdProvider : IUserIdProvider
{
    public string GetUserId(HubConnectionContext connection)
    {
        return connection.User.FindFirst("userId")!.Value;
    }
}