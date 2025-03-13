package ru.hse.cardefectscan.client

import mu.KLogging
import org.openapi.cardefectscan.model.ImageRequestElement
import org.openapi.modelservice.model.ImageProcessRequest
import org.openapi.modelservice.api.ImagesApi as ModelApi
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import ru.hse.cardefectscan.configuration.AsyncConfiguration.Companion.MODEL_CLIENT_EXECUTOR
import ru.hse.cardefectscan.repository.ImageRequestRepository
import ru.hse.cardefectscan.service.TransactionHelper
import ru.hse.cardefectscan.service.image.ImageName
import ru.hse.cardefectscan.service.image.ImageService
import kotlin.jvm.optionals.getOrNull

@Component
class ModelClientAsync(
    private val modelClient: ModelApi,
    private val transactionHelper: TransactionHelper,
    private val imageRequestRepository: ImageRequestRepository,
    private val imageService: ImageService,
) {
    @Async(MODEL_CLIENT_EXECUTOR)
    fun sendRequestToModel(imageName: ImageName) {
        logger.info { "Requesting model to process request with key ${imageName.filename}" }
        transactionHelper.launch {
            imageRequestRepository
                .findById(imageName.filename).getOrNull()!!
                .apply { status = ImageRequestElement.Status.IN_PROGRESS }
            val request = requestForProcess(imageName)
            modelClient.apiV1ProcessRequestPost(request)
        }
    }

    private fun requestForProcess(imageName: ImageName) = ImageProcessRequest(
        jobId = imageName.filename,
        resultName = ImageName(imageName.filename, imageName.userId, ImageName.PROCESSED_FOLDER).toString(),
        downloadLink = imageService.downloadLink(imageName),
    )

    companion object : KLogging()
}