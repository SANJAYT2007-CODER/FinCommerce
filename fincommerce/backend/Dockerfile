
# Stage 1: Build the application using Maven + Java 21
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests
 
# Stage 2: Run the application using a lightweight JRE
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
 
# Render assigns the port via the PORT environment variable
ENV PORT=8080
EXPOSE 8080
 
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]