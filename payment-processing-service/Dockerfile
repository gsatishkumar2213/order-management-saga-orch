FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/payment-processing-service-0.0.1-SNAPSHOT.jar payment-processing-service.jar
EXPOSE 9091
ENTRYPOINT ["java", "-jar","payment-processing-service.jar"]