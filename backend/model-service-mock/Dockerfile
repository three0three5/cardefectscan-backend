FROM openjdk:21-slim
WORKDIR /app
COPY ./build/libs/*.jar mockserver.jar
COPY ./build/libs/javaagent/opentelemetry-javaagent.jar /usr/src/app/javaagent.jar

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar mockserver.jar"]