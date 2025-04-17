# Stage 1: Build the application
FROM maven:3.9.4-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copy all files and build the application
COPY pom.xml .
COPY checkstyle.xml .
COPY src ./src

# Package the application (skip tests optionally)
RUN mvn clean install -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jdk-alpine

VOLUME /tmp

EXPOSE 8080

# Copy the built jar from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Run the jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]
