# Use an official Maven image to build the project
FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app

# Copy the pom.xml and the source code
COPY pom.xml .
COPY src ./src

# Package the application
RUN mvn clean package

# Use a smaller base image to run the application
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the packaged application to the Docker image
COPY --from=build /app/target/*.jar app.jar

# Specify the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
