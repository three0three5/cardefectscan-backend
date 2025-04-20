package ru.hse.cardefectscan.client

import mu.KLogging
import org.openapi.cardefectscan.model.ImageRequestStatus
import org.openapi.modelservice.model.ImageProcessRequest
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import ru.hse.cardefectscan.configuration.AsyncConfiguration.Companion.MODEL_CLIENT_EXECUTOR
import ru.hse.cardefectscan.repository.ImageRequestRepository
import ru.hse.cardefectscan.service.image.ImageName
import kotlin.jvm.optionals.getOrNull
import org.openapi.modelservice.api.ImagesApi as ModelApi

@Component
class ModelClientAsync(
    private val modelClient: ModelApi,
    private val imageRequestRepository: ImageRequestRepository,
) {
    @Async(MODEL_CLIENT_EXECUTOR)
    fun sendRequestToModel(imageName: ImageName) {
        logger.info { "Requesting model to process request with key ${imageName.filename}" }
        val imgRequest = imageRequestRepository
            .findById(imageName.filename).getOrNull()!!
            .apply { status = ImageRequestStatus.IN_PROGRESS }
        val request = requestForProcess(imageName)
        runCatching {
            modelClient.apiV1ProcessRequestPost(request)
        }.onFailure {
            imgRequest.apply {
                status = ImageRequestStatus.FAILED
                description = "Сервис с моделью недоступен. Попробуйте повторить запрос позже"
            }
        }
        imageRequestRepository.save(imgRequest)
    }

    private fun requestForProcess(imageName: ImageName) = ImageProcessRequest(
        jobId = imageName.filename,
        resultName = ImageName(imageName.filename, imageName.userId, ImageName.PROCESSED_FOLDER).toString(),
        downloadObjectName = imageName.toString(),
    )

    companion object : KLogging()
}