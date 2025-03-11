package ru.hse.cardefectscan.listener

import mu.KLogging
import org.springframework.amqp.AmqpRejectAndDontRequeueException
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import ru.hse.cardefectscan.handler.event.MinioEvent
import ru.hse.cardefectscan.handler.event.ModelEvent

@Component
class AppQueueListener(
    private val eventPublisher: ApplicationEventPublisher,
) {

    @RabbitListener(queues = ["app-queue"])
    fun listen(message: Message) {
        val body = String(message.body, Charsets.UTF_8)
        val headers = message.messageProperties.headers
        val messageType = MessageType.fromHeaders(headers)
        if (messageType == null) {
            logger.warn { "Unrecognized message type with headers: $headers" }
            return
        }
        logger.info { "Received message: $body" }
        logger.info { "Headers: $headers" }
        runCatching {
            when (messageType) {
                MessageType.MINIO_EVENT -> eventPublisher.publishEvent(MinioEvent(this, body))
                MessageType.MODEL_SERVICE_EVENT -> eventPublisher.publishEvent(ModelEvent(this, body))
            }
        }.onFailure {
            throw AmqpRejectAndDontRequeueException(it)
        }
    }

    companion object : KLogging()
}

enum class MessageType {
    MINIO_EVENT,
    MODEL_SERVICE_EVENT;

    companion object {
        private const val MINIO_EVENT_HEADER_KEY = "minio-event"
        private const val MODEL_HEADER_KEY = "model-service"

        fun fromHeaders(headers: Map<String, Any>): MessageType? {
            for ((key, _) in headers) {
                val messageType = fromString(key)
                if (messageType != null) return messageType
            }
            return null
        }

        private fun fromString(value: String): MessageType? {
            return when (value) {
                MINIO_EVENT_HEADER_KEY -> MINIO_EVENT;
                MODEL_HEADER_KEY -> MODEL_SERVICE_EVENT;
                else -> null
            }
        }
    }
}
