package ru.hse.cardefectscan

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import ru.hse.cardefectscan.properties.AppProperties

@SpringBootApplication
@EnableConfigurationProperties(AppProperties::class)
class CardefectscanApplication

fun main(args: Array<String>) {
    runApplication<CardefectscanApplication>(*args)
}
