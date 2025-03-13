FROM openjdk:21-slim
WORKDIR /app
COPY ./build/libs/*.jar mockserver.jar

ENTRYPOINT ["java", "-jar", "mockserver.jar"]