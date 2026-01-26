FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/quotes-service-1.0.0.jar quotes-service.jar
EXPOSE 8091
ENTRYPOINT ["java","-jar","quotes-service.jar"]
