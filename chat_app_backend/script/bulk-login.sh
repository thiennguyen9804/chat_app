#!/bin/bash

# Cấu hình
LOGIN_URL="http://localhost:8080/api/auth/login"
OUTPUT_FILE="users.json"
NUM_USERS=1000
CONCURRENCY_LIMIT=20
TEMP_DIR="temp_tokens"

# Tạo thư mục tạm
mkdir -p $TEMP_DIR
rm -f $TEMP_DIR/*.json

login_user() {
    local user_id=$1
    local username=""
    local password="1"

    # Logic đặt tên theo yêu cầu của bạn
    if [ "$user_id" -eq 1 ]; then
        username="Kiana Kaslana"
    elif [ "$user_id" -eq 2 ]; then
        username="Raiden Mei"
    elif [ "$user_id" -eq 3 ]; then
        username="Bronya Zaychik"
    else
        username="Test_User_${user_id}"
    fi

    # Gọi API bằng curl
    # -s: silent, -w: ghi định dạng output (lấy http_code ở dòng cuối)
    response=$(curl -s -w "\n%{http_code}" -X POST "$LOGIN_URL" \
        -H "Content-Type: application/json" \
        -d "{\"username\":\"$username\", \"password\":\"$password\", \"region\": \"ASIA\" }" \
        --connect-timeout 10
    )

    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)

    if [ "$http_code" -eq 200 ]; then
        token=$(echo "$body" | jq -r '.token // .accessToken')
        echo "{\"id\": \"$user_id\", \"username\": \"$username\", \"token\": \"$token\"}" > "$TEMP_DIR/user_$user_id.json"
        echo "✅ $username (ID: $user_id): Thành công"
    else
        echo "❌ $username (ID: $user_id): Lỗi $http_code"
    fi
}

echo "--- Bắt đầu Login $NUM_USERS users (Gồm bộ ba Valkyrie) ---"

for ((i=1; i<=$NUM_USERS; i++)); do
    login_user "$i" &

    # Giới hạn số lượng tiến trình chạy ngầm
    if [[ $(jobs -r -p | wc -l) -ge $CONCURRENCY_LIMIT ]]; then
        wait -n
    fi
done

wait

# Gộp các file JSON lẻ thành mảng duy nhất bằng jq
echo "--- Đang tổng hợp dữ liệu vào $OUTPUT_FILE ---"
jq -s 'sort_by(.id | tonumber)' $TEMP_DIR/*.json > $OUTPUT_FILE

# Dọn dẹp
rm -rf $TEMP_DIR

echo "--- Hoàn tất! ---"