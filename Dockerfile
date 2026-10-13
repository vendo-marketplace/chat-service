FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /build

COPY pom.xml ./

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

RUN ls -lh target

FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY --from=build /build/target/chat-service*.jar chat-service.jar

EXPOSE 9030

CMD ["java", "-jar", "chat-service.jar"]
