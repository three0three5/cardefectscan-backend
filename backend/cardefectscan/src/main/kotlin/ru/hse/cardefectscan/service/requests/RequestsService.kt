package ru.hse.cardefectscan.service.requests

import mu.KLogging
import org.openapi.cardefectscan.model.ImageRequestDetailed
import org.openapi.cardefectscan.model.ImageRequestElement
import org.openapi.cardefectscan.model.ImageRequestStatus
import org.openapi.cardefectscan.model.PageRequestResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ru.hse.cardefectscan.entity.ImageRequestEntity
import ru.hse.cardefectscan.exception.ImageNotFoundException
import ru.hse.cardefectscan.repository.ImageRequestRepository
import ru.hse.cardefectscan.repository.UserRepository
import ru.hse.cardefectscan.service.image.ImageName
import ru.hse.cardefectscan.service.image.ImageName.Companion.LOADED_FOLDER
import ru.hse.cardefectscan.service.image.ImageName.Companion.PROCESSED_FOLDER
import ru.hse.cardefectscan.service.image.LinkComposer
import ru.hse.cardefectscan.service.security.AuthDetailsService
import java.time.ZoneOffset

@Service
class RequestsService(
    private val authDetailsService: AuthDetailsService,
    private val imageRequestRepository: ImageRequestRepository,
    private val userRepository: UserRepository,
    private val linkComposer: LinkComposer,
) {
    fun getPaginatedRequests(page: Int, size: Int): ResponseEntity<PageRequestResponse> {
        val userId = authDetailsService.getCurrentUser().userId
        val user = userRepository.getReferenceById(userId)
        val pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending())
        val result = imageRequestRepository.findAllByUser(user, pageRequest)
        return ResponseEntity.ok(
            createResponse(result)
        )
    }

    private fun createResponse(page: Page<ImageRequestEntity>): PageRequestResponse {
        val content = page.content.map {
            ImageRequestElement(
                imageId = it.imageName,
                status = it.status,
                createdAt = it.createdAt.atOffset(ZoneOffset.UTC),
                thumbnailLink = linkComposer.thumbnail(it.imageName),
            )
        }
        return PageRequestResponse(
            content = content,
            totalPages = page.totalPages,
            totalElements = page.totalElements,
            currentPage = page.number,
            pageSize = page.size,
        )
    }

    fun getDetailedInfo(imageId: String): ResponseEntity<ImageRequestDetailed> {
        val userId = authDetailsService.getCurrentUser().userId
        val user = userRepository.getReferenceById(userId)
        val image = imageRequestRepository.findByImageNameAndUser(imageId, user) ?: throw ImageNotFoundException()
        if (image.status == ImageRequestStatus.IMAGE_LOADING) throw ImageNotFoundException()
        val imageNameOriginal = ImageName(
            image.imageName,
            userId,
            folderName = LOADED_FOLDER
        )
        val imageNameResult = ImageName(
            image.imageName,
            userId,
            folderName = PROCESSED_FOLDER,
        )
        return ResponseEntity.ok(
            ImageRequestDetailed(
                imageId = image.imageName,
                createdAt = image.createdAt.atOffset(ZoneOffset.UTC),
                updatedAt = image.updatedAt.atOffset(ZoneOffset.UTC),
                status = image.status,
                originalImageDownloadLink = linkComposer.proxiedLink(imageNameOriginal),
                resultImageDownloadLink = linkComposer.proxiedLink(imageNameResult),
            )
        )
    }

    companion object : KLogging()
}