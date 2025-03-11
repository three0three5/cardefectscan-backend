package ru.hse.cardefectscan.service.image

import io.minio.GetPresignedObjectUrlArgs
import io.minio.MinioClient
import io.minio.http.Method
import mu.KLogging
import org.openapi.cardefectscan.model.ImageLink
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ru.hse.cardefectscan.entity.ImageRequestEntity
import ru.hse.cardefectscan.properties.MinioProperties
import ru.hse.cardefectscan.repository.ImageRequestRepository
import ru.hse.cardefectscan.repository.UserRepository
import ru.hse.cardefectscan.service.image.ImageName.Companion.LOADED_FOLDER
import ru.hse.cardefectscan.service.security.AuthDetailsService
import java.util.UUID

@Service
class ImageService(
    private val authDetailsService: AuthDetailsService,
    private val minioClient: MinioClient,
    private val minioProperties: MinioProperties,
    private val imageRequestRepository: ImageRequestRepository,
    private val userRepository: UserRepository,
) {
    fun generateLoadLink(): ResponseEntity<ImageLink> {
        val currentUserId = authDetailsService.getCurrentUser().userId
        val user = userRepository.getReferenceById(currentUserId)
        val imageName = generateFileName(currentUserId, LOADED_FOLDER)
        logger.info { "image name: $imageName" }
        val imageRequest = ImageRequestEntity(
            user = user,
            imageName = imageName.filename
        )
        val url = minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .bucket(minioProperties.bucket)
                .`object`(imageName.toString())
                .method(Method.PUT)
                .expiry(minioProperties.putLinkExpiration)
                .build()
        )
        logger.info { "generated url: $url" }
        imageRequestRepository.save(imageRequest)
        return ResponseEntity.ok(ImageLink(url))
    }

    fun generateFileName(
        userId: Long,
        folderName: String
    ): ImageName = ImageName(UUID.randomUUID().toString(), userId, folderName)

    fun downloadLink(imageName: ImageName): String {
        return minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(minioProperties.bucket)
                .`object`(imageName.toString())
                .expiry(minioProperties.getLinkExpiration)
                .build()
        )
    }

    companion object : KLogging()
}