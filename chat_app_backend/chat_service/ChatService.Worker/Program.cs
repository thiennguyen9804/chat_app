using ChatService.Worker;
using RabbitMQ.Client;
using StackExchange.Redis;

var builder = Host.CreateApplicationBuilder(args);
string redisConnectionString = builder.Configuration.GetConnectionString("Redis")!;
builder.Services.AddSignalR()
    .AddStackExchangeRedis(redisConnectionString, options => {
        options.Configuration.ChannelPrefix = "ChatApp";
    });
builder.Services.AddSingleton<IConnectionMultiplexer>(sp =>
{
    return ConnectionMultiplexer.Connect(redisConnectionString);
});
builder.Services.AddHostedService<ChatExchangeConsumer>();
var factory = new ConnectionFactory { 
    HostName = "localhost", 
    UserName = "guest", 
    Password = "guest" 
};

var connection = await factory.CreateConnectionAsync();
builder.Services.AddSingleton(connection);
var host = builder.Build();
host.Run();
