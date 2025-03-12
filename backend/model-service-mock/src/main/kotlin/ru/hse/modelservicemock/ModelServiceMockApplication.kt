package ru.hse.modelservicemock

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ModelServiceMockApplication

fun main(args: Array<String>) {
    runApplication<ModelServiceMockApplication>(*args)
}
