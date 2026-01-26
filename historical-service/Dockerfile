FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/historical-service-*.jar historical-service.jar
EXPOSE 8094
ENTRYPOINT ["java","-jar","historical-service.jar"]
