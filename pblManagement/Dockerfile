# Use the official Eclipse Temurin image for Java 25
FROM eclipse-temurin:25-jdk-alpine

LABEL maintainer="buianhtu.com"

# Specify the main jar and avoid the "original" one
COPY target/*-0.0.1-SNAPSHOT.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]