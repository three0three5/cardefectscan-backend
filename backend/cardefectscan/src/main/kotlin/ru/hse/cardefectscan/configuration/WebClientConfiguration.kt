package ru.hse.cardefectscan.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient


@Configuration
class WebClientConfiguration {
    @Bean
    fun webClient(): WebClient {
        return WebClient.builder().build()
    }
}