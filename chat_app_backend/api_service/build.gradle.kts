plugins {
    java
    id("org.springframework.boot") version "4.0.6" // Lưu ý: Spring Boot hiện tại bản ổn định là 3.x, bạn nên kiểm tra lại bản 4.0.6
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    
    implementation("com.github.f4b6a3:ulid-creator:5.2.4")
    
    implementation(platform("io.jsonwebtoken:jjwt-bom:0.13.0"))
    implementation("io.jsonwebtoken:jjwt-api")
    runtimeOnly("io.jsonwebtoken:jjwt-impl")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson")
    
    implementation(platform("io.awspring.cloud:spring-cloud-aws-dependencies:3.1.1"))    
    implementation("io.awspring.cloud:spring-cloud-aws-starter-dynamodb")
    
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

