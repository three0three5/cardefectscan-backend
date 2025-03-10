package ru.hse.cardefectscan.controller

import org.openapi.cardefectscan.api.ImagesApi
import org.openapi.cardefectscan.model.ImageLink
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import ru.hse.cardefectscan.service.image.ImageService

@RestController
class ImagesController(
    private val imageService: ImageService,
) : ImagesApi {
    override fun apiV1ImagesLoadGet(): ResponseEntity<ImageLink> {
        return imageService.generateLoadLink()
    }
}