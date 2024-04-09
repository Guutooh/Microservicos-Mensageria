FROM maven:3.6.3-openjdk-17 as build
WORKDIR /app
COPY . .
RUN mvn clean package

FROM openjdk:17
WORKDIR /app
COPY --from=build ./app/target/*.jar ./app.jar
#COPY ./target/eurekaserver-0.0.1-SNAPSHOT.jar app.jar

ARG RABBITMQ=rabbitmq
ARG EUREKA_SERVER=eureka-server

ENTRYPOINT java -jar app.jar