# -------- Stage 1: Build the app --------
FROM gradle:jdk17-alpine AS builder
WORKDIR /app
COPY . .
RUN gradle clean bootJar --no-daemon -x test

# -------- Stage 2: Run the app --------
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]