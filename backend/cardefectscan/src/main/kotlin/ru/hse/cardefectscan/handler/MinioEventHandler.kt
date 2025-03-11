package ru.hse.cardefectscan.handler

import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Service
import ru.hse.cardefectscan.handler.event.MinioEvent

@Service
class MinioEventHandler(
) : ApplicationListener<MinioEvent> {
    override fun onApplicationEvent(event: MinioEvent) {
        TODO("Not yet implemented")
    }
}