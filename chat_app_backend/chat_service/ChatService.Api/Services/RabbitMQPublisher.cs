using System.Text;
using System.Text.Encodings.Web;
using System.Text.Json;
using Microsoft.AspNetCore.Authorization;
using RabbitMQ.Client;
using ChatService.Shared;

[Authorize]
public class RabbitMQPublisher : IRabbitMQPublisher
{
    private readonly IConnection _connection;
    private IChannel? _channel;
    private readonly string _queueName = "chat_messages";
    public RabbitMQPublisher(IConnection connection)
    {
        _connection = connection;
    }

    private async Task InitChannel()
    {
        // Khởi tạo channel 1 lần duy nhất hoặc tạo lại nếu bị đóng
        if (_channel == null || _channel.IsClosed)
        {
            _channel = await _connection.CreateChannelAsync();
            await _channel.QueueDeclareAsync(queue: _queueName, durable: true, exclusive: false, autoDelete: false);
        }
    }

    public async Task PublishMessageAsync<T>(T message)
    {
        await InitChannel();
        var options = new JsonSerializerOptions
        {
            Encoder = JavaScriptEncoder.UnsafeRelaxedJsonEscaping,
            WriteIndented = false
        };
        var json = JsonSerializer.Serialize(message, options);
        var body = Encoding.UTF8.GetBytes(json);
        await _channel!.BasicPublishAsync(
            exchange: string.Empty,
            routingKey: _queueName,
            body: body
        );
    }
}