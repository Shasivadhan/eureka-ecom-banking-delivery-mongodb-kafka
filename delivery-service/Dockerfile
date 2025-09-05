# Use official JDK image
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy jar from target (after mvn package)
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Expose port (change per service)
EXPOSE 8080

# Run jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
