package ru.hse.cardefectscan.service.image

import io.minio.GetPresignedObjectUrlArgs
import io.minio.MinioClient
import io.minio.http.Method
import org.openapi.cardefectscan.model.ImageLink
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ru.hse.cardefectscan.properties.MinioProperties
import ru.hse.cardefectscan.service.security.AuthDetailsService
import java.util.UUID

@Service
class ImageService(
    private val authDetailsService: AuthDetailsService,
    private val minioClient: MinioClient,
    private val minioProperties: MinioProperties,
) {
    fun generateLoadLink(): ResponseEntity<ImageLink> {
        val currentUserId = authDetailsService.getCurrentUser()
        TODO()
        val url = minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .bucket(minioProperties.bucket)
                .`object`(generateFileName())
                .method(Method.GET)
                .expiry(minioProperties.expiration)
                .build()
        )
        return ResponseEntity.ok(ImageLink(url))
    }

    fun generateFileName(): String = UUID.randomUUID().toString()
}