ENDPOINT="http://localhost:8000"
REGION="us-east-1"
PROFILE="local"
PASSWORD='$2a$12$CfG/cYno1ImEA9qV4K72JecasU6TNe9T01N11.p2kzH6zT26vVNTm'

echo "--- Đang nạp dữ liệu Users ---"

aws dynamodb batch-write-item \
    --request-items '{
        "Users": [
            {
                "PutRequest": {
                    "Item": {
                        "UserId": {"S": "1"},
                        "Username": {"S": "Kiana Kaslana"},
                        "Password": {"S": "'"$PASSWORD"'"}
                    }
                }
            },
            {
                "PutRequest": {
                    "Item": {
                        "UserId": {"S": "2"},
                        "Username": {"S": "Raiden Mei"},
                        "Password": {"S": "'"$PASSWORD"'"}
                    }
                }
            },
            {
                "PutRequest": {
                    "Item": {
                        "UserId": {"S": "3"},
                        "Username": {"S": "Bronya Zaychik"},
                        "Password": {"S": "'"$PASSWORD"'"}
                    }
                }
            }
        ]
    }' \
    --endpoint-url $ENDPOINT --profile $PROFILE

echo "--- Đang tạo tin nhắn mẫu giữa Kiana và Mei ---"