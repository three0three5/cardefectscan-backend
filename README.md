# CarDefectScan

Для деплоя (предварительно создав .env файл в папке с compose файлом):

1.

```bash
cd backend/cardefectscan
docker compose up -d -f prod.compose.yml
```

В случае необходимости, можно собрать образ локально:

1.

```bash
cd backend/cardefectscan
./gradlew downloadOpenTelemetryJavaAgent
docker build -t orobtsovv/cardefectscan:latest .
```

Для локального запуска серверной части при разработке:

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

Переменные окружения:

```
POSTGRES_PASSWORD=
POSTGRES_USER=
MINIO_ROOT_PASSWORD=
MINIO_ROOT_USER=
MINIO_BUCKET_NAME=
RABBITMQ_USER=
RABBITMQ_PASSWORD=
APP_HOST=http://localhost:8080
APP_DOMAIN=localhost
GF_USER=
GF_PASSWORD=
JWT_PRIVATE_KEY=
```