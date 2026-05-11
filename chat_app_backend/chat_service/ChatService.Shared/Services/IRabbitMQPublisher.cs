namespace ChatService.Shared;
public interface IRabbitMQPublisher
{
    Task PublishMessageAsync<T>(T message);
}