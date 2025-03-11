package ru.hse.cardefectscan.handler.event

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.openapi.cardefectscan.model.S3Event
import org.springframework.context.ApplicationEvent

class MinioEvent(
    source: Any,
    private val message: String,
) : ApplicationEvent(source) {
    private val objectMapper = JsonMapper.builder()
        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .build()

    fun toS3Event(): S3Event {
        return objectMapper.readValue(message)
    }

    companion object {
        const val PUT_EVENT_NAME = "s3:ObjectCreated:Put"
    }
}