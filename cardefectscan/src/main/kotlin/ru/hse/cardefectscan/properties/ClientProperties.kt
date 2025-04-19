package ru.hse.cardefectscan.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "client")
data class ClientProperties @ConstructorBinding constructor(
    val modelServiceHost: String,
)