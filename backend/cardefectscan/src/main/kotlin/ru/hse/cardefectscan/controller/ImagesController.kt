package ru.hse.cardefectscan.controller

import org.openapi.cardefectscan.api.ImagesApi
import org.openapi.cardefectscan.model.ImageLink
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import ru.hse.cardefectscan.service.image.HashService
import ru.hse.cardefectscan.service.image.ImageService

@RestController
class ImagesController(
    private val imageService: ImageService,
    private val hashChecker: HashService,
) : ImagesApi {
    override fun apiV1ImagesLoadGet(): ResponseEntity<ImageLink> {
        return imageService.generateUploadLink()
    }

    override fun apiV1ImagesGet(imageId: String, hash: String): ResponseEntity<Resource> {
        return hashChecker.withCheck(imageId, hash) {
            imageService.getImageByImageName(imageId)
        }
    }
}