package ru.hse.cardefectscan.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "auth")
data class AuthProperties @ConstructorBinding constructor(
    val refreshLifespan: Long,
    val jwtLifespan: Long,
)