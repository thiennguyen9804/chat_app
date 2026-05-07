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
var factory = new ConnectionFactory 
{ 
    HostName = builder.Configuration["RabbitMQ:HostName"], 
    Port = int.Parse(builder.Configuration["RabbitMQ:Port"] ?? "5672"),
    UserName = builder.Configuration["RabbitMQ:UserName"], 
    Password = builder.Configuration["RabbitMQ:Password"]
};
var connection = await factory.CreateConnectionAsync();
builder.Services.AddSingleton(connection);
builder.Services.AddHostedService<ChatExchangeConsumer>();
var host = builder.Build();
host.Run();
