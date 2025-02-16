FROM openjdk:21-slim
WORKDIR /app
COPY ./build/libs/*.jar backend.jar

ENTRYPOINT ["java", "-jar", "backend.jar"]