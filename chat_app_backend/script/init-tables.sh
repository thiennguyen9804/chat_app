#!/bin/bash

ENDPOINT="http://localhost:8000"
REGION="us-east-1"
PROFILE="local"

echo "--- Khởi tạo bảng Users ---"

aws dynamodb create-table \
    --table-name Users \
    --attribute-definitions \
        AttributeName=UserId,AttributeType=S \
    --key-schema \
        AttributeName=UserId,KeyType=HASH \
    --provisioned-throughput \
        ReadCapacityUnits=5,WriteCapacityUnits=5 \
    --endpoint-url $ENDPOINT \
    --region $REGION \
    --profile $PROFILE

echo "--- Khởi tạo bảng ChatMessages (Không Stream) ---"

aws dynamodb create-table \
    --table-name Messages \
    --attribute-definitions \
        AttributeName=ChatId,AttributeType=S \
        AttributeName=MessageId,AttributeType=S \
    --key-schema \
        AttributeName=ChatId,KeyType=HASH \
        AttributeName=MessageId,KeyType=RANGE \
    --provisioned-throughput \
        ReadCapacityUnits=5,WriteCapacityUnits=5 \
    --endpoint-url $ENDPOINT \
    --region $REGION \
    --profile $PROFILE

echo "--- Hoàn tất ---"