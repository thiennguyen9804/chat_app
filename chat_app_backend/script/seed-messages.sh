#!/bin/bash
ENDPOINT="http://localhost:8000"
REGION="us-east-1"
PROFILE="local"
MESSAGE_ID='1_2'
# MessageId là "1#2" (Sắp xếp theo ID nhỏ đến lớn để thống nhất 1 phòng chat)
echo "--- Đang nạp 20 tin nhắn riêng tư giữa Kiana và Mei ---"

aws dynamodb batch-write-item \
    --endpoint-url http://localhost:8000 \
    --request-items '{
        "Messages": [
            {"PutRequest": {"Item": {"MessageId": {"S": "'$MESSAGE_ID'"}, "ChatId": {"S": "1714987120000#m3"}, "SenderId": {"S": "1"}, "ReceiverId": {"S": "2"}, "Type": {"S": "TEXT"}, "Content": {"S": "Mei-senpai ơi, chị đang ở bếp rồi ạ?"}, "Timestamp": {"N": "1714987120000"}}}},
            {"PutRequest": {"Item": {"MessageId": {"S": "'$MESSAGE_ID'"}, "ChatId": {"S": "1714987180000#m4"}, "SenderId": {"S": "2"}, "ReceiverId": {"S": "1"}, "Type": {"S": "TEXT"}, "Content": {"S": "Ừm, chị đang chuẩn bị nguyên liệu đây. Sao thế em?"}, "Timestamp": {"N": "1714987180000"}}}},
            {"PutRequest": {"Item": {"MessageId": {"S": "'$MESSAGE_ID'"}, "ChatId": {"S": "1714987240000#m5"}, "SenderId": {"S": "1"}, "ReceiverId": {"S": "2"}, "Type": {"S": "TEXT"}, "Content": {"S": "Dạ không có gì, chỉ là em muốn xuống giúp chị một tay thôi!"}, "Timestamp": {"N": "1714987240000"}}}},
            {"PutRequest": {"Item": {"MessageId": {"S": "'$MESSAGE_ID'"}, "ChatId": {"S": "1714987300000#m6"}, "SenderId": {"S": "2"}, "ReceiverId": {"S": "1"}, "Type": {"S": "TEXT"}, "Content": {"S": "Hì hì, Kiana giúp chị thì bếp sẽ náo nhiệt lắm đây."}, "Timestamp": {"N": "1714987300000"}}}},
            {"PutRequest": {"Item": {"MessageId": {"S": "'$MESSAGE_ID'"}, "ChatId": {"S": "1714987360000#m7"}, "SenderId": {"S": "1"}, "ReceiverId": {"S": "2"}, "Type": {"S": "TEXT"}, "Content": {"S": "Hix, chị lại trêu em rồi. Lần này em sẽ làm cẩn thận mà!"}, "Timestamp": {"N": "1714987360000"}}}},
            {"PutRequest": {"Item": {"MessageId": {"S": "'$MESSAGE_ID'"}, "ChatId": {"S": "1714987420000#m8"}, "SenderId": {"S": "2"}, "ReceiverId": {"S": "1"}, "Type": {"S": "TEXT"}, "Content": {"S": "Chị tin em mà. Vậy xuống giúp chị nhặt rau nhé?"}, "Timestamp": {"N": "1714987420000"}}}},
            {"PutRequest": {"Item": {"MessageId": {"S": "'$MESSAGE_ID'"}, "ChatId": {"S": "1714987480000#m9"}, "SenderId": {"S": "1"}, "ReceiverId": {"S": "2"}, "Type": {"S": "TEXT"}, "Content": {"S": "Tuân lệnh! Em xuống ngay đây ạ!"}, "Timestamp": {"N": "1714987480000"}}}},
            {"PutRequest": {"Item": {"MessageId": {"S": "'$MESSAGE_ID'"}, "ChatId": {"S": "1714987540000#m10"}, "SenderId": {"S": "1"}, "ReceiverId": {"S": "2"}, "Type": {"S": "TEXT"}, "Content": {"S": "À mà Mei-senpai, hôm nay chị đi học có mệt không?"}, "Timestamp": {"N": "1714987540000"}}}},
            {"PutRequest": {"Item": {"MessageId": {"S": "'$MESSAGE_ID'"}, "ChatId": {"S": "1714987600000#m11"}, "SenderId": {"S": "2"}, "ReceiverId": {"S": "1"}, "Type": {"S": "TEXT"}, "Content": {"S": "Cũng hơi mệt một chút, nhưng thấy em hào hứng thế này chị hết mệt rồi."}, "Timestamp": {"N": "1714987600000"}}}},
            {"PutRequest": {"Item": {"MessageId": {"S": "'$MESSAGE_ID'"}, "ChatId": {"S": "1714987660000#m12"}, "SenderId": {"S": "1"}, "ReceiverId": {"S": "2"}, "Type": {"S": "TEXT"}, "Content": {"S": "Thương chị quá... Để lát nữa em bóp vai cho chị nha."}, "Timestamp": {"N": "1714987660000"}}}},
            {"PutRequest": {"Item": {"MessageId": {"S": "'$MESSAGE_ID'"}, "ChatId": {"S": "1714987720000#m13"}, "SenderId": {"S": "2"}, "ReceiverId": {"S": "1"}, "Type": {"S": "TEXT"}, "Content": {"S": "Cảm ơn Kiana của chị nhiều nhé."}, "Timestamp": {"N": "1714987720000"}}}},
            {"PutRequest": {"Item": {"MessageId": {"S": "'$MESSAGE_ID'"}, "ChatId": {"S": "1714987780000#m14"}, "SenderId": {"S": "1"}, "ReceiverId": {"S": "2"}, "Type": {"S": "TEXT"}, "Content": {"S": "Ước gì ngày nào cũng được ăn cơm chị nấu."}, "Timestamp": {"N": "1714987780000"}}}},
            {"PutRequest": {"Item": {"MessageId": {"S": "'$MESSAGE_ID'"}, "ChatId": {"S": "1714987840000#m15"}, "SenderId": {"S": "2"}, "ReceiverId": {"S": "1"}, "Type": {"S": "TEXT"}, "Content": {"S": "Thì chị vẫn nấu cho em mỗi ngày mà, đồ ngốc."}, "Timestamp": {"N": "1714987840000"}}}},
            {"PutRequest": {"Item": {"MessageId": {"S": "'$MESSAGE_ID'"}, "ChatId": {"S": "1714987900000#m16"}, "SenderId": {"S": "1"}, "ReceiverId": {"S": "2"}, "Type": {"S": "TEXT"}, "Content": {"S": "Hehe, em sẽ không bao giờ chán đâu!"}, "Timestamp": {"N": "1714987900000"}}}},
            {"PutRequest": {"Item": {"MessageId": {"S": "'$MESSAGE_ID'"}, "ChatId": {"S": "1714987960000#m17"}, "SenderId": {"S": "2"}, "ReceiverId": {"S": "1"}, "Type": {"S": "TEXT"}, "Content": {"S": "Mau xuống đi, cá sắp chín tới nơi rồi đấy."}, "Timestamp": {"N": "1714987960000"}}}},
            {"PutRequest": {"Item": {"MessageId": {"S": "'$MESSAGE_ID'"}, "ChatId": {"S": "1714988020000#m18"}, "SenderId": {"S": "1"}, "ReceiverId": {"S": "2"}, "Type": {"S": "TEXT"}, "Content": {"S": "Em đang chạy xuống cầu thang đây! Chờ em!"}, "Timestamp": {"N": "1714988020000"}}}},
            {"PutRequest": {"Item": {"MessageId": {"S": "'$MESSAGE_ID'"}, "ChatId": {"S": "1714988080000#m19"}, "SenderId": {"S": "2"}, "ReceiverId": {"S": "1"}, "Type": {"S": "TEXT"}, "Content": {"S": "Cẩn thận kẻo ngã đấy Kiana."}, "Timestamp": {"N": "1714988080000"}}}},
            {"PutRequest": {"Item": {"MessageId": {"S": "'$MESSAGE_ID'"}, "ChatId": {"S": "1714988140000#m20"}, "SenderId": {"S": "1"}, "ReceiverId": {"S": "2"}, "Type": {"S": "TEXT"}, "Content": {"S": "Em thấy mùi thơm rồi! Tuyệt quá đi mất!"}, "Timestamp": {"N": "1714988140000"}}}},
            {"PutRequest": {"Item": {"MessageId": {"S": "'$MESSAGE_ID'"}, "ChatId": {"S": "1714988200000#m21"}, "SenderId": {"S": "2"}, "ReceiverId": {"S": "1"}, "Type": {"S": "TEXT"}, "Content": {"S": "Rửa tay rồi vào bàn ngồi đi em."}, "Timestamp": {"N": "1714988200000"}}}},
            {"PutRequest": {"Item": {"MessageId": {"S": "'$MESSAGE_ID'"}, "ChatId": {"S": "1714988260000#m22"}, "SenderId": {"S": "1"}, "ReceiverId": {"S": "2"}, "Type": {"S": "TEXT"}, "Content": {"S": "Vâng ạ! Chúc Mei-senpai một buổi tối vui vẻ!"}, "Timestamp": {"N": "1714988260000"}}}}
        ]
    }' \
    --profile $PROFILE
echo "--- Hoàn tất Seeding Valkyries! ---"