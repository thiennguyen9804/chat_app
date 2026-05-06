
using Amazon.DynamoDBv2.DataModel;

[DynamoDBTable("Messages")]
public class ChatMessage
{
    [DynamoDBHashKey]
    public String MessageId { get; set; }
    [DynamoDBRangeKey] // Sort Key (ULID)
    public string ChatId { get; set; }

    public int SenderId { get; set; }
    public int ReceiverId { get; set; }
    public string Content { get; set; }
    public string Type { get; set; }
    public long Timestamp { get; set; }
}