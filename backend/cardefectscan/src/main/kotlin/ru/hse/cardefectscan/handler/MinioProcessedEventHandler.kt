package ru.hse.cardefectscan.handler

import mu.KLogging
import org.openapi.cardefectscan.model.ImageRequestElement
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Service
import ru.hse.cardefectscan.handler.event.MinioEvent
import ru.hse.cardefectscan.handler.event.MinioEvent.Companion.PUT_EVENT_NAME
import ru.hse.cardefectscan.repository.ImageRequestRepository
import ru.hse.cardefectscan.service.image.ImageName
import ru.hse.cardefectscan.service.image.ImageName.Companion.PROCESSED_FOLDER
import kotlin.jvm.optionals.getOrNull

@Service
class MinioProcessedEventHandler(
    private val imageRequestRepository: ImageRequestRepository,
) : ApplicationListener<MinioEvent> {
    override fun onApplicationEvent(event: MinioEvent) {
        val dto = event.toS3Event()
        val key = dto.key ?: return
        val imageName = ImageName.fromStringWithBucket(key)
        if (dto.eventName != PUT_EVENT_NAME || imageName.folderName != PROCESSED_FOLDER) {
            logger.info { "non put or 'processed' folder, so ignore" }
            return
        }
        val entity = imageRequestRepository.findById(imageName.filename).getOrNull()
        if (entity == null || entity.status != ImageRequestElement.Status.IN_PROGRESS) {
            logger.warn { "Received suspicious image request with key $imageName" }
            return
        }
        entity.status = ImageRequestElement.Status.DONE
        imageRequestRepository.save(entity)
    }

    companion object : KLogging()
}