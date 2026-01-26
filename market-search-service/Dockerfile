FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/market-search-service-1.0.0.jar market-search-service.jar
EXPOSE 8090
ENTRYPOINT ["java","-jar","market-search-service.jar"]
