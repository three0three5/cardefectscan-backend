package ru.hse.cardefectscan.listener

import mu.KLogging
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class MinioQueueListener {

    @RabbitListener(queues = ["minio-queue"])
    fun listen(message: String) {
        logger.info { "Received message from minio-queue: $message" }
    }

    companion object : KLogging()
}