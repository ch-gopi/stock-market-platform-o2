# Use an official lightweight JDK runtime as base
FROM eclipse-temurin:17-jdk-alpine

# Set working directory inside container
WORKDIR /app

# Copy the JAR file into the container
COPY target/watchlist-service-1.0.0.jar app.jar

# Expose the port your service runs on (adjust if different)
EXPOSE 8076

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
