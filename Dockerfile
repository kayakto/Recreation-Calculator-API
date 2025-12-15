FROM gradle:8.10.2-jdk17-alpine

WORKDIR /app

COPY . /app

RUN gradle build -x test

CMD ["java", "-jar", "build/libs/recreation-calculator-1.0.0.jar"]
