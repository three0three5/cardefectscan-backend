# CarDefectScan

Для локального запуска серверной части:

1. 

```bash
cd backend/cardefectscan
./gradlew clean build
```

2. 

```bash
docker compose up -d
```

3. запустить с помощью

```bash
 ./gradlew bootRunLocal
```

4. после завершения вернуться в исходную директорию

```bash
cd ..
cd ..
```

В случае необходимости, можно создать образ mockserver локально:

1.

```bash
cd backend/model-service-mock
./gradlew clean build
cd ..
cd ..
```

2.

```bash
docker build -t orobtsovv/model-service-mock:latest ./backend/model-service-mock
```

SwaggerUI доступен по ссылке: http://localhost:8081/

RabbitMQ UI: http://localhost:15672/

Minio UI: http://localhost:9001/