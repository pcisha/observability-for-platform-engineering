# Build stage
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the JAR file from build stage
COPY --from=builder /app/target/platform-request-service-0.1.0.jar app.jar

# Install curl and download OpenTelemetry Java Agent
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/* \
    && curl -L -o opentelemetry-javaagent.jar https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.5.0/opentelemetry-javaagent.jar

# Expose the application port
EXPOSE 8080

# Run with OpenTelemetry agent
ENTRYPOINT ["java", "-javaagent:./opentelemetry-javaagent.jar", "-jar", "app.jar"]