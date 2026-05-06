using Amazon.DynamoDBv2.DataModel;
using Microsoft.AspNetCore.SignalR;

public class ChatHub : Hub
{
    private readonly IDynamoDBContext _db;
    public ChatHub(IDynamoDBContext db) => _db = db;

    public async Task SendMessage(int senderId, int receiverId, string content)
    {
        var messageId = senderId < receiverId ? $"{senderId}_{receiverId}" : $"{receiverId}_{senderId}";
        var newMessage = new ChatMessage
        {
            MessageId = messageId,
            ChatId = Ulid.NewUlid().ToString(), // Dùng thư viện Ulid.Net
            SenderId = senderId,
            ReceiverId = receiverId,
            Type = "TEXT",
            Content = content,
        };
        await _db.SaveAsync(newMessage);

    }

}