# Build stage
FROM gradle:8.5-jdk21 AS builder

COPY . /src
WORKDIR /src
RUN gradle bot-app:clean bot-app:jar --no-daemon

# Runtime stage
FROM gcr.io/distroless/java21-debian12:latest

LABEL org.opencontainers.image.source=https://github.com/cafe-des-artistes/bot
LABEL org.opencontainers.image.description="Cafe des Artistes Discord Music Bot"
LABEL org.opencontainers.image.licenses=MIT

# Copy the fat JAR from build stage
COPY --from=builder /src/bot-app/build/libs/bot-app-1.0.0-SNAPSHOT.jar /app.jar

# Expose metrics and health check port
EXPOSE 8080

# Set JVM options for container environment
ENV JAVA_TOOL_OPTIONS="-XX:+UseZGC -XX:+UnlockExperimentalVMOptions -XX:MaxRAMPercentage=75.0"

# Run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]