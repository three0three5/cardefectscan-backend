package ru.hse.cardefectscan.listener

import mu.KLogging
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class AppQueueListener {

    @RabbitListener(queues = ["app-queue"])
    fun listen(message: Message) {
        val body = String(message.body, Charsets.UTF_8)
        val headers = message.messageProperties.headers

        logger.info { "Received message: $body" }
        logger.info { "Headers: $headers" }
    }

    companion object : KLogging()
}

enum class MessageType {
    MINIO_EVENT,
    NEURAL_NETWORK_SERVICE_EVENT,
}
