FROM maven:3.6.3-openjdk-17 as build
WORKDIR /app
COPY . .
RUN mvn clean package

FROM openjdk:17
WORKDIR /app
COPY --from=build ./app/target/*.jar ./app.jar
#COPY ./target/eurekaserver-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8761
ENTRYPOINT java -jar app.jar