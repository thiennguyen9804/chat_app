// 1. Tạo class Provider để ánh xạ Claim "userId"
using Microsoft.AspNetCore.SignalR;

public class CustomUserIdProvider(ILogger<CustomUserIdProvider> logger) : IUserIdProvider
{
    private readonly ILogger _logger = logger;
    public string GetUserId(HubConnectionContext connection)
    {
        Console.WriteLine($"Claim Len: {connection.User.Claims.Count()}");
        foreach (var claim in connection.User.Claims)
        {
            Console.WriteLine($"Type: {claim.Type} - Value: {claim.Value}");
        }

        return connection.User.FindFirst("userId")!.Value;
    }
}