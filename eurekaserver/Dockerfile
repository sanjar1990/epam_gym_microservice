FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/eurekaserver-0.0.1-SNAPSHOT.jar eureka-service.jar

ENTRYPOINT ["java", "-jar", "eureka-service.jar"]