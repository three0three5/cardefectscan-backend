package ru.hse.cardefectscan.configuration

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.QueueBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMqConfiguration {

    @Bean
    fun appQueue(): Queue {
        return QueueBuilder.durable(APP_QUEUE)
            .withArgument("x-dead-letter-exchange", "")
            .withArgument("x-dead-letter-routing-key", QUEUE_MESSAGES_DLQ)
            .build();
    }

    @Bean
    fun minioExchange(): FanoutExchange {
        return FanoutExchange("minio-events", true, false)
    }

    @Bean
    fun binding(): Binding {
        return BindingBuilder
            .bind(appQueue()).to(minioExchange())
    }

    @Bean
    fun deadLetterQueue(): Queue {
        return QueueBuilder.durable(QUEUE_MESSAGES_DLQ).build()
    }

    companion object {
        const val APP_QUEUE = "app-queue"
        const val QUEUE_MESSAGES_DLQ = "app-queue-dlq"
    }

}
