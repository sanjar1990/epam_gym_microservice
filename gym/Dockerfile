FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/gym-0.0.1-SNAPSHOT.jar main-service.jar

ENTRYPOINT ["java", "-jar", "main-service.jar"]