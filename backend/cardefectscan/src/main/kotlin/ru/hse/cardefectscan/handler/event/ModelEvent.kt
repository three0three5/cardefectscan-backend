package ru.hse.cardefectscan.handler.event

import org.springframework.context.ApplicationEvent

class ModelEvent(
    source: Any,
    private val message: String,
) : ApplicationEvent(source) {

}