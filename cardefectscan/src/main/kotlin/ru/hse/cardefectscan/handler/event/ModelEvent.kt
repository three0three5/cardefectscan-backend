package ru.hse.cardefectscan.handler.event

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.openapi.modelservice.model.EventMessage
import org.springframework.context.ApplicationEvent

class ModelEvent(
    source: Any,
    private val message: String,
) : ApplicationEvent(source) {
    private val objectMapper = JsonMapper.builder()
        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .addModule(KotlinModule.Builder().build())
        .build()

    fun toEventMessage(): EventMessage {
        return objectMapper.readValue(message)
    }
}