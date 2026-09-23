# Stage 1: Build the Spring Boot application using Maven
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal JRE Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# JVM Performance & Low-Memory Tuning for Free Tier Containers (512MB RAM)
ENV JAVA_TOOL_OPTIONS="-Xmx350m -Xss512k -XX:+UseSerialGC -Dfile.encoding=UTF-8"
ENV PORT=8080

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
