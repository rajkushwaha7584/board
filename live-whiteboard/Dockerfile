# Use OpenJDK 17 (lightweight)
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the built JAR
COPY target/live-whiteboard-0.0.1-SNAPSHOT.jar app.jar

# Expose Spring Boot port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java","-jar","app.jar"]
