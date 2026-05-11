using Amazon.DynamoDBv2.DataModel;
using Microsoft.AspNetCore.SignalR;
namespace ChatService.Shared;

public class ChatHub(IDynamoDBContext db, IRabbitMQPublisher publisher) : Hub
{
    private readonly IDynamoDBContext _db = db;
    private readonly IRabbitMQPublisher _publisher = publisher;
    
    public override async Task OnConnectedAsync() {
        var userId = Context.UserIdentifier;
        Console.WriteLine($"[Hub] Thiết bị mới kết nối. UserID: {userId ?? "Vô danh"}");
        await base.OnConnectedAsync();
    }
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
        _ = _publisher.PublishMessageAsync(newMessage);
    }

}