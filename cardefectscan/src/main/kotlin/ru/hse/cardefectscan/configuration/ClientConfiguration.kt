package ru.hse.cardefectscan.configuration

import org.openapi.modelservice.api.ImagesApi
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.hse.cardefectscan.properties.ClientProperties

@EnableConfigurationProperties(ClientProperties::class)
@Configuration
class ClientConfiguration(
    private val clientProperties: ClientProperties,
) {
    @Bean
    fun modelServiceApi() = ImagesApi(clientProperties.modelServiceHost)
}