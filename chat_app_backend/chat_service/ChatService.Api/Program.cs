using System.Text;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DataModel;
using Amazon.Runtime;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.SignalR;
using Microsoft.IdentityModel.Tokens;
using RabbitMQ.Client;
using ChatService.Shared;
using Steeltoe.Discovery.Eureka;

var builder = WebApplication.CreateBuilder(args);
string redisConnectionString = builder.Configuration.GetConnectionString("Redis")!;
builder.Services
    .AddSignalR()
    .AddStackExchangeRedis(redisConnectionString, options => {
        options.Configuration.ChannelPrefix = "ChatApp";
    });


var credentials = new BasicAWSCredentials("fakeKey", "fakeSecret");
var awsOptions = builder.Configuration.GetAWSOptions();
awsOptions.Credentials = credentials;

builder.Services.AddDefaultAWSOptions(awsOptions);
builder.Services.AddAWSService<IAmazonDynamoDB>();
builder.Services.AddScoped<IDynamoDBContext, DynamoDBContext>();
builder.Services.AddSingleton<IUserIdProvider, CustomUserIdProvider>();
builder.Services
    .AddAuthentication(options => {
        options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme; 
        options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
    })
    .AddJwtBearer(options => {
            options.TokenValidationParameters = new TokenValidationParameters
            {
                ValidateIssuerSigningKey = true,
                IssuerSigningKey = new SymmetricSecurityKey(
                    Encoding.UTF8.GetBytes(builder.Configuration["Jwt:Key"]!)
                ),
                ValidateIssuer = false,
                ValidateActor = false,
                ValidateLifetime = false,
                ValidateAudience = false,
            };
            options.Events = new JwtBearerEvents
            {
                OnMessageReceived = context =>
                {
                    var accessToken = context.Request.Query["access_token"];
                    Console.WriteLine($"Access Token: {accessToken}");
                    var path = context.HttpContext.Request.Path;
                    if(!string.IsNullOrEmpty(path) && path.StartsWithSegments("/chat-socket"))  
                    {
                        context.Token = accessToken;
                    }

                    return Task.CompletedTask;
                }
            };
    });
var factory = new ConnectionFactory();
builder.Configuration.GetSection("RabbitMQ").Bind(factory);
var connection = await factory.CreateConnectionAsync();
builder.Services.AddSingleton(connection);
builder.Services.AddSingleton<IRabbitMQPublisher, RabbitMQPublisher>();

builder.Services.AddEurekaDiscoveryClient();
var app = builder.Build();

app.MapHub<ChatHub>("/chat-socket");

app.UseAuthentication();
app.UseAuthorization();

app.Run();