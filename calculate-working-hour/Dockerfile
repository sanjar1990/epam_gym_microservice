FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/workload-0.0.1-SNAPSHOT.jar report-service.jar

ENTRYPOINT ["java", "-jar", "report-service.jar"]