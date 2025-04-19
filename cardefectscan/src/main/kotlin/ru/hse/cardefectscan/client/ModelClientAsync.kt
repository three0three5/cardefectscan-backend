package ru.hse.cardefectscan.client

import mu.KLogging
import org.openapi.cardefectscan.model.ImageRequestStatus
import org.openapi.modelservice.model.ImageProcessRequest
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import ru.hse.cardefectscan.configuration.AsyncConfiguration.Companion.MODEL_CLIENT_EXECUTOR
import ru.hse.cardefectscan.repository.ImageRequestRepository
import ru.hse.cardefectscan.service.TransactionHelper
import ru.hse.cardefectscan.service.image.ImageName
import kotlin.jvm.optionals.getOrNull
import org.openapi.modelservice.api.ImagesApi as ModelApi

@Component
class ModelClientAsync(
    private val modelClient: ModelApi,
    private val transactionHelper: TransactionHelper,
    private val imageRequestRepository: ImageRequestRepository,
) {
    @Async(MODEL_CLIENT_EXECUTOR)
    fun sendRequestToModel(imageName: ImageName) {
        logger.info { "Requesting model to process request with key ${imageName.filename}" }
        transactionHelper.launch {
            imageRequestRepository
                .findById(imageName.filename).getOrNull()!!
                .apply { status = ImageRequestStatus.IN_PROGRESS }
            val request = requestForProcess(imageName)
            modelClient.apiV1ProcessRequestPost(request) // todo outbox of IMAGE_LOADED, when fail then FAILED
        }
    }

    private fun requestForProcess(imageName: ImageName) = ImageProcessRequest(
        jobId = imageName.filename,
        resultName = ImageName(imageName.filename, imageName.userId, ImageName.PROCESSED_FOLDER).toString(),
        downloadObjectName = imageName.toString(),
    )

    companion object : KLogging()
}