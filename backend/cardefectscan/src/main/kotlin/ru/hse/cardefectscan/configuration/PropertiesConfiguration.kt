package ru.hse.cardefectscan.configuration

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import ru.hse.cardefectscan.properties.AuthProperties

@Configuration
@EnableConfigurationProperties(AuthProperties::class)
class PropertiesConfiguration