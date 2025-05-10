package ru.hse.cardefectscan.handler

import mu.KLogging
import org.openapi.cardefectscan.model.ImageRequestStatus
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.hse.cardefectscan.handler.event.ModelEvent
import ru.hse.cardefectscan.repository.ImageRequestRepository
import kotlin.jvm.optionals.getOrNull

@Service
class ModelEventHandler(
    private val imageRequestRepository: ImageRequestRepository,
) : ApplicationListener<ModelEvent> {
    @Transactional
    override fun onApplicationEvent(event: ModelEvent) {
        val dto = event.toEventMessage()
        if (!dto.isError) return
        val entity = imageRequestRepository.findById(dto.jobId).getOrNull()
        if (entity == null) {
            logger.warn { "received non existing request from model with jobId ${dto.jobId}" }
            return
        }
        entity.status = ImageRequestStatus.FAILED
        entity.description = dto.info
        imageRequestRepository.save(entity)
    }

    companion object : KLogging()
}