package ru.hse.cardefectscan.service.image

import mu.KLogging
import org.openapi.cardefectscan.model.ImageLink
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ru.hse.cardefectscan.entity.ImageRequestEntity
import ru.hse.cardefectscan.exception.ImageNotFoundException
import ru.hse.cardefectscan.repository.ImageRequestRepository
import ru.hse.cardefectscan.repository.UserRepository
import ru.hse.cardefectscan.service.image.ImageName.Companion.LOADED_FOLDER
import ru.hse.cardefectscan.service.security.AuthDetailsService
import java.util.UUID

@Service
class ImageService(
    private val authDetailsService: AuthDetailsService,
    private val imageRequestRepository: ImageRequestRepository,
    private val userRepository: UserRepository,
    private val linkComposer: LinkComposer,
) {
    fun generateUploadLink(): ResponseEntity<ImageLink> {
        val currentUserId = authDetailsService.getCurrentUser().userId
        val user = userRepository.getReferenceById(currentUserId)
        val imageName = generateFileName(currentUserId)
        logger.info { "image name: $imageName" }
        val imageRequest = ImageRequestEntity(
            user = user,
            imageName = imageName.filename
        )
        val url = linkComposer.linkForPut(imageName)
        logger.info { "generated url: $url" }
        imageRequestRepository.save(imageRequest)
        return ResponseEntity.ok(ImageLink(url))
    }

    fun getImageByImageName(folder: String, filename: String, hash: String): ResponseEntity<Resource> {
        val userId = authDetailsService.getCurrentUser().userId
        val imageName = ImageName(
            userId = userId,
            filename = filename,
            folderName = folder,
        )
        return linkComposer.withCheck(imageName, hash) {
            try {
                logger.info { "imageName: $imageName" }
                val stream = linkComposer.getObjectStream(imageName)
                val resource = InputStreamResource { stream }
                val headers = HttpHeaders().apply {
                    stream.headers().forEach {
                        add(it.first, it.second)
                    }
                }
                ResponseEntity.ok()
                    .headers(headers)
                    .body(resource)
            } catch (e: Exception) {
                logger.warn { "Exception occurred: ${e.message}" }
                throw ImageNotFoundException()
            }
        }
    }

    private fun generateFileName(
        userId: Long,
    ): ImageName = ImageName(UUID.randomUUID().toString(), userId, LOADED_FOLDER)

    companion object : KLogging()
}