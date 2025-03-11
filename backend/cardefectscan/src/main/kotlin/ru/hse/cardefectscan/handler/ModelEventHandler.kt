package ru.hse.cardefectscan.handler

import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Service
import ru.hse.cardefectscan.handler.event.ModelEvent

@Service
class ModelEventHandler(
) : ApplicationListener<ModelEvent> {
    override fun onApplicationEvent(event: ModelEvent) {
        TODO("Not yet implemented")
    }
}