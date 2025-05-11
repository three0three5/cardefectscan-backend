# CarDefectScan

Для деплоя (предварительно создав .env файл в папке с compose файлом):

1.

```bash
cd cardefectscan
mkdir volumes
mkdir volumes/loki-data
mkdir volumes/prometheus-data
mkdir volumes/grafana-data
docker compose -f prod.compose.yml up -d
cd ..
```

В случае необходимости, можно собрать образ локально:

1.

```bash
cd cardefectscan
./gradlew clean build
./gradlew downloadOpenTelemetryJavaAgent
docker build -t orobtsovv/cardefectscan:latest .
cd ..
```

Для локального запуска серверной части при разработке:

1. 

```bash
cd cardefectscan
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
```

В случае необходимости, можно создать образ mockserver локально:

1.

```bash
cd model-service-mock
./gradlew clean build
./gradlew downloadOpenTelemetryJavaAgent
docker build -t orobtsovv/model-service-mock:latest .
cd ..
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
IS_COOKIE_HTTP_ONLY=
IS_COOKIE_SECURE=
JWT_LIFESPAN=
```
