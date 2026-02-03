FROM maven:3.8.1-openjdk-17-slim AS builder
VOLUME -v
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests




FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/target/order-service-0.0.1-SNAPSHOT.jar order-service.jar
EXPOSE 9089
ENTRYPOINT ["java", "-jar","order-service.jar"]