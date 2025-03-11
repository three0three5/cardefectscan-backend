package ru.hse.cardefectscan.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "minio")
data class MinioProperties @ConstructorBinding constructor(
    val endpoint: String,
    val accessKey: String,
    val secretKey: String,
    val bucket: String,
    val putLinkExpiration: Int,
)