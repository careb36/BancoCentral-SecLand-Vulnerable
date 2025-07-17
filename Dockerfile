# Stage 1: Build with Maven
# Use a Maven image with Java 21 to build the project
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# Copy only the pom.xml to download dependencies efficiently
COPY pom.xml .
# Download dependencies without executing build
RUN mvn dependency:go-offline -B
# Copy the rest of the source code and build the JAR
COPY src ./src
RUN mvn package -DskipTests -B

# Stage 2: Run the application
# Use a lightweight Java image to run the application
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Copy the built JAR from the previous stage
COPY --from=build /app/target/*.jar app.jar
# Expose port 8080 for external access
EXPOSE 8080
# Command to start the application when the container launches
ENTRYPOINT ["java","-jar","app.jar"]
