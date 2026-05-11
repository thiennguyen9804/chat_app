using System.Text;
using System.Text.Json;
using Microsoft.AspNetCore.SignalR;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;
using StackExchange.Redis;
using ChatService.Shared;


namespace ChatService.Worker;

public class ChatExchangeConsumer(IConnectionMultiplexer redis, IHubContext<ChatHub> hubContext, IConnection connection) : BackgroundService
{
    private readonly IConnection _connection = connection;
    private readonly IHubContext<ChatHub> _hubContext = hubContext;
    private IChannel? _channel;
    private readonly string _queueName = "chat_messages";
    private readonly IDatabase _redisDb = redis.GetDatabase();

    

    private async Task InitChannel()
    {
        // Khởi tạo channel 1 lần duy nhất hoặc tạo lại nếu bị đóng
        if (_channel == null || _channel.IsClosed)
        {
            _channel = await _connection.CreateChannelAsync();
            await _channel.QueueDeclareAsync(queue: _queueName, durable: true, exclusive: false, autoDelete: false);
        }
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        await InitChannel();
        var consumer = new AsyncEventingBasicConsumer(_channel!);
        consumer.ReceivedAsync += async (model, ea) =>
        {
            var body = ea.Body.ToArray();
            var messageJson = Encoding.UTF8.GetString(body);
            var chatMsg = JsonSerializer.Deserialize<ChatMessage>(messageJson);
            Console.WriteLine($"Có tin nhắn: {messageJson}");
            string presenceKey = $"user:{chatMsg.ReceiverId}:status";
            bool isOnline = await _redisDb.KeyExistsAsync(presenceKey);
            if(isOnline)
            {
                try
                {
                    Console.WriteLine($"[Worker] Đã nhận tin: {messageJson}");
                    await hubContext.Clients.User(chatMsg.ReceiverId.ToString())
                        .SendAsync("ReceiveMessage", chatMsg.SenderId, chatMsg.Content);
                    await _channel!.BasicAckAsync(ea.DeliveryTag, false);
                } catch (Exception ex)
                {
                    Console.WriteLine($"[Error] Lỗi xử lý tin nhắn: {ex.Message}");
                    await _channel!.BasicNackAsync(ea.DeliveryTag, false, true);
                }
            } else
            {
                Console.WriteLine($"[Push Notification] Gửi thông báo với nội dung: {messageJson}");
            }
                
        };

        await _channel!.BasicConsumeAsync(queue: _queueName, autoAck: false, consumer: consumer);

        await Task.Delay(Timeout.Infinite, stoppingToken);
    }

    public override async Task StopAsync(CancellationToken cancellationToken)
    {
        if (_channel != null) await _channel.CloseAsync();
        await base.StopAsync(cancellationToken);
    }
}
