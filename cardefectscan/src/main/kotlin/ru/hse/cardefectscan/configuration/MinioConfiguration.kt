package ru.hse.cardefectscan.configuration

import io.minio.MinioClient
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.hse.cardefectscan.properties.MinioProperties

@Configuration
@EnableConfigurationProperties(MinioProperties::class)
class MinioConfiguration {
    @Bean
    fun minioClient(
        minioProperties: MinioProperties,
    ): MinioClient = MinioClient.builder()
        .endpoint(minioProperties.endpoint)
        .credentials(minioProperties.accessKey, minioProperties.secretKey)
        .build()
}