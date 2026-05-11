ENDPOINT="http://localhost:8000"
REGION="us-east-1"
PROFILE="local"
PASSWORD='$2a$12$CfG/cYno1ImEA9qV4K72JecasU6TNe9T01N11.p2kzH6zT26vVNTm'
TABLE_NAME="Users"

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
echo "--- Bắt đầu Seeding 997 Users ---"

# Bắt đầu từ ID 4 vì 1, 2, 3 đã có rồi
# Kết thúc ở 1000
batch_items=""
count=0

for i in {4..1000}
do
    # Tạo JSON cho từng Item
    item='{"PutRequest": {"Item": {"UserId": {"S": "'$i'"}, "Username": {"S": "Test_User_'$i'"}, "Password": {"S": "'$PASSWORD'"}}}}'
    
    if [ -z "$batch_items" ]; then
        batch_items="$item"
    else
        batch_items="$batch_items, $item"
    fi
    
    ((count++))

    # DynamoDB batch-write-item giới hạn tối đa 25 items một lần
    if [ $count -eq 25 ]; then
        echo "Đang nạp lô 25 users..."
        aws dynamodb batch-write-item \
            --request-items '{"'$TABLE_NAME'": ['"$batch_items"']}' \
            --endpoint-url $ENDPOINT --profile $PROFILE
        
        # Reset lại biến sau khi gửi
        batch_items=""
        count=0
    fi
done

# Nạp nốt số user còn dư (nếu có)
if [ $count -gt 0 ]; then
    echo "Đang nạp số users còn lại..."
    aws dynamodb batch-write-item \
        --request-items '{"'$TABLE_NAME'": ['"$batch_items"']}' \
        --endpoint-url $ENDPOINT --profile $PROFILE
fi

echo "--- Hoàn tất Seeding! ---"