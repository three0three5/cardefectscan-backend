package ru.hse.cardefectscan.configuration

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.core.Queue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMqConfiguration {

    @Bean
    fun minioQueue(): Queue {
        return Queue("minio-queue", true)
    }

    @Bean
    fun minioExchange(): FanoutExchange {
        return FanoutExchange("minio-events", true, false)
    }

    @Bean
    fun binding(minioQueue: Queue, minioExchange: FanoutExchange): Binding {
        return BindingBuilder.bind(minioQueue).to(minioExchange)
    }
}
