FROM eclipse-temurin:17-jdk-alpine as builder
WORKDIR /app
COPY . .
RUN gradle clean build -x test  # ← Изменено с mvn

FROM eclipse-temurin:17-jre-alpine
COPY --from=builder /app/build/libs/recreation-calculator-1.0.0.jar app.jar
