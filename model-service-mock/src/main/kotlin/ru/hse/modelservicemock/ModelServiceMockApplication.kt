package ru.hse.modelservicemock

import io.minio.MinioClient
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
@EnableConfigurationProperties(MinioProperties::class)
class ModelServiceMockApplication {
    @Bean
    fun minioClient(
        minioProperties: MinioProperties,
    ): MinioClient = MinioClient.builder()
        .endpoint(minioProperties.endpoint)
        .credentials(minioProperties.accessKey, minioProperties.secretKey)
        .build()
}

fun main(args: Array<String>) {
    runApplication<ModelServiceMockApplication>(*args)
}
