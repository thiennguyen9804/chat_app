# THÌ (STAGE) 1: BUILD
# Sử dụng SDK để biên dịch code
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src

# Copy file .csproj và restore dependencies (tận dụng cache của Docker)
COPY ["chat_service/ChatService.Api/ChatService.Api.csproj", "chat_service/ChatService.Api/"]
RUN dotnet restore "chat_service/ChatService.Api/ChatService.Api.csproj"

# Copy toàn bộ mã nguồn và build
COPY . .
WORKDIR "/src/chat_service/ChatService.Api"
RUN dotnet build "ChatService.Api.csproj" -c Release -o /app/build

# THÌ (STAGE) 2: PUBLISH
# Xuất bản ứng dụng ra file thực thi
FROM build AS publish
RUN dotnet publish "ChatService.Api.csproj" -c Release -o /app/publish /p:UseAppHost=false

# THÌ (STAGE) 3: RUNTIME
# Sử dụng bản Runtime nhẹ để chạy ứng dụng
FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS final
WORKDIR /app
COPY --from=publish /app/publish .

# Cấu hình Port cho container (Khớp với cấu hình Kestrel của bạn)
ENV ASPNETCORE_URLS=http://+:8081 
EXPOSE 8081

ENTRYPOINT ["dotnet", "ChatService.Api.dll"]