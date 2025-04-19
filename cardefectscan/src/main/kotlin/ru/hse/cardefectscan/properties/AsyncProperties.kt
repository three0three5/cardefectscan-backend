package ru.hse.cardefectscan.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "async")
data class AsyncProperties @ConstructorBinding constructor(
    val modelClient: AsyncCommonProperties,
)

data class AsyncCommonProperties(
    val corePoolSize: Int = 1,
    val maxPoolSize: Int = 5,
    val queueCapacity: Int = 10,
)