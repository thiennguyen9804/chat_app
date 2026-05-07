# Stage 1: Build stage
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Copy các file cấu hình gradle trước để tận dụng Docker cache
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle .

# Cấp quyền thực thi cho gradlew và tải dependencies
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

# Copy mã nguồn và build dự án
COPY src src
RUN ./gradlew bootJar --no-daemon

# Stage 2: Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy file jar đã build từ Stage 1
COPY --from=build /app/build/libs/*.jar app.jar

# Biến môi trường mặc định (có thể ghi đè khi chạy container)
ENV JAVA_OPTS="-Xms256m -Xmx512m"

EXPOSE 8080

# Lệnh chạy ứng dụng
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]