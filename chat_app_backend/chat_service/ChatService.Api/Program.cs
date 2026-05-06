using System.Text;
using Amazon.DynamoDBv2;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.SignalR;
using Microsoft.IdentityModel.Tokens;
using RabbitMQ.Client;

var builder = WebApplication.CreateBuilder(args);
string redisConnectionString = builder.Configuration.GetConnectionString("Redis")!;
builder.Services
    .AddSignalR()
    .AddStackExchangeRedis(redisConnectionString);

var awsOptions = builder.Configuration.GetAWSOptions();
builder.Services.AddDefaultAWSOptions(awsOptions);
builder.Services.AddAWSService<IAmazonDynamoDB>();
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
                ValidateActor = false
            };
            options.Events = new JwtBearerEvents
            {
                OnMessageReceived = context =>
                {
                    var accessToken = context.Request.Query["access_token"];
                    var path = context.HttpContext.Request.Path;
                    if(!string.IsNullOrEmpty(path) && path.Equals("chat-socket"))
                    {
                        context.Token = accessToken;
                    }

                    return Task.CompletedTask;
                }
            };
    });
var factory = new ConnectionFactory { 
    HostName = "localhost", 
    UserName = "guest", 
    Password = "guest" 
};

var connection = await factory.CreateConnectionAsync();
builder.Services.AddSingleton(connection);

builder.Services.AddSingleton<IRabbitMQPublisher, RabbitMQPublisher>();



var app = builder.Build();

app.MapHub<ChatHub>("/chat-socket");

app.UseAuthentication();
app.UseAuthorization();


app.Run();