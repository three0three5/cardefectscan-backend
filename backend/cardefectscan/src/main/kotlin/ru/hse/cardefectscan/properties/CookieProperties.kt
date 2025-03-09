package ru.hse.cardefectscan.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "auth.cookies")
data class CookieProperties @ConstructorBinding constructor(
    val maxAge: Int,
    val isHttpOnly: Boolean,
    val isSecure: Boolean,
)