# # Use an official Maven image to build the project
# FROM maven:3.8.5-openjdk-17-slim AS build
# WORKDIR /app

# # Copy the pom.xml and the source code
# COPY pom.xml .
# COPY src ./src

# # Package the application
# RUN mvn clean package

# # Use a smaller base image to run the application
# FROM openjdk:17-jdk-slim
# WORKDIR /app

# # Copy the packaged application to the Docker image
# COPY --from=build /app/target/*.jar app.jar

# # Specify the command to run the application
# ENTRYPOINT ["java", "-jar", "app.jar"]
##############################################################
# # Use an official Maven image to build the project
# FROM maven:3.8.5-openjdk-17-slim AS build
# WORKDIR /app

# # Copy the pom.xml and the source code
# COPY pom.xml .
# COPY src ./src

# # Package the application
# RUN mvn clean package

# # Use a smaller base image to run the application
# FROM openjdk:17-jdk-slim
# WORKDIR /app

# # Copy the packaged application to the Docker image
# COPY --from=build /app/target/test_aurora_iam-1.0-SNAPSHOT.jar app.jar

# # Specify the command to run the application
# ENTRYPOINT ["java", "-jar", "app.jar"]
###################################################
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

# Install SSH client
RUN apt-get update && apt-get install -y openssh-client

# Copy the packaged application to the Docker image
COPY --from=build /app/target/test_aurora_iam-1.0-SNAPSHOT.jar app.jar

# Copy the SSH private key into the container
# Ensure you replace 'KEY_FOR_BASTION_HOST.pem' with your actual key filename
COPY KEY_FOR_BASTION_HOST.pem /root/.ssh/KEY_FOR_BASTION_HOST.pem

# Ensure the private key has the correct permissions
RUN chmod 400 /root/.ssh/KEY_FOR_BASTION_HOST.pem

# Specify the command to run the SSH tunnel and start the Java application
# CMD ssh -i /root/.ssh/KEY_FOR_BASTION_HOST.pem -N -L 5433:tempdb.cluster-cu4jou8grpwg.us-east-1.rds.amazonaws.com:5432 -p 22 ec2-user@54.87.152.135 & java -jar app.jar
CMD ssh -i /root/.ssh/KEY_FOR_BASTION_HOST.pem -o StrictHostKeyChecking=no -N -L 5433:tempdb.cluster-cu4jou8grpwg.us-east-1.rds.amazonaws.com:5432 -p 22 ec2-user@54.87.152.135 & java -jar app.jar
