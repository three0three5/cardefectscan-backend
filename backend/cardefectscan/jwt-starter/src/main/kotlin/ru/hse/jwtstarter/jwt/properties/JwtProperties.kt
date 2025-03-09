package ru.hse.jwtstarter.jwt.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "auth.jwt")
data class JwtProperties @ConstructorBinding constructor(
    val refreshLifespan: Long,
    val lifespan: Long,
    val privateKey: String,
)