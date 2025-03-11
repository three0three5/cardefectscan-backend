package ru.hse.cardefectscan.handler.event

import org.springframework.context.ApplicationEvent

class MinioEvent(
    source: Any,
    private val message: String,
) : ApplicationEvent(source) {
}