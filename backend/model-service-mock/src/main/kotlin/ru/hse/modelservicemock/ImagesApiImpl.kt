package ru.hse.modelservicemock

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes
import io.minio.MinioClient
import io.minio.PutObjectArgs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import mu.KLogging
import org.openapi.modelservice.api.ImagesApi
import org.openapi.modelservice.model.EventMessage
import org.openapi.modelservice.model.ImageProcessRequest
import org.openapi.modelservice.model.ResultList
import org.openapi.modelservice.model.ResultMetadata
import org.springframework.amqp.core.MessageBuilder
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.core.io.ClassPathResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.time.Duration

val backgroundScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

@RestController
class ImagesApiImpl(
    private val opsService: OpsService,
    private val rabbitTemplate: RabbitTemplate,
    private val objectMapper: ObjectMapper,
    private val minioClient: MinioClient,
    private val minioProperties: MinioProperties,
) : ImagesApi {
    override fun apiV1ProcessRequestPost(imageProcessRequest: ImageProcessRequest): ResponseEntity<Unit> {
        logger.info { "received request for processing image: $imageProcessRequest" }
        backgroundScope.launch {
            logger.info { "trying to download image" }
            downloadImage(imageProcessRequest.downloadLink, "testloading/" + imageProcessRequest.resultName + ".png")
            handle(imageProcessRequest)
        }
        return ResponseEntity.ok().build()
    }

    private suspend fun downloadImage(downloadLink: String, s: String) {
        val client = HttpClient(CIO)

        try {
            val byteArray = client.get(downloadLink).readBytes()

            val file = File(s)

            if (!file.parentFile.exists()) {
                val dirsCreated = file.parentFile.mkdirs()
                if (!dirsCreated) {
                    logger.error { "Could not create dirs for image" }
                }
            }

            logger.info { "trying to write bytes to path $s" }
            file.writeBytes(byteArray)
            logger.info { "image loaded to $s" }
        } catch (e: IOException) {
            logger.error(e) { "Exception while image loading" }
        } finally {
            client.close()
        }
    }

    private suspend fun handle(imageProcessRequest: ImageProcessRequest) {
        logger.info { "delayed for ${opsService.delay} seconds" }
        delay(Duration.ofSeconds(opsService.delay.toLong()))
        if (opsService.toFail) {
            logger.info { "initiating fail" }
            initiateFail(imageProcessRequest)
        } else {
            logger.info { "initiating success" }
            saveResult(imageProcessRequest)
        }
    }

    private suspend fun initiateFail(imageProcessRequest: ImageProcessRequest) {
        val body = EventMessage(
            jobId = imageProcessRequest.jobId,
            isError = true,
            info = "something went wrong",
        )
        val message = MessageBuilder
            .withBody(objectMapper.writeValueAsString(body).toByteArray())
            .setHeader("model-service", true)
            .build()
        logger.info { "sending message with body: $body" }
        rabbitTemplate.convertAndSend("minio-events", "", message)
    }

    private suspend fun saveResult(imageProcessRequest: ImageProcessRequest) {
        val metadata = ResultList(
            listOf(
                mapOf(
                    "0" to ResultMetadata(ResultMetadata.DamageLevel.NONE, "empty"),
                    "1" to ResultMetadata(ResultMetadata.DamageLevel.SCRATCH, "left_headlight"),
                    "2" to ResultMetadata(ResultMetadata.DamageLevel.CRACK, "right_headlight"),
                    "3" to ResultMetadata(ResultMetadata.DamageLevel.DENT, "front_bumper"),
                )
            )
        )
        val jsonMetadata = objectMapper.writeValueAsString(metadata)
        uploadImage("mask.png", imageProcessRequest.resultName, jsonMetadata)
        logger.info { "finished successfully" }
    }

    private fun uploadImage(imageName: String, resultName: String, jsonMetadata: String) {
        logger.info { "uploading image $resultName" }
        val resource = ClassPathResource(imageName)
        val inputStream: InputStream = resource.inputStream
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(minioProperties.bucket)
                .`object`(resultName)
                .stream(inputStream, resource.contentLength(), -1)
                .contentType("image/png")
                .userMetadata(mapOf("json-data" to jsonMetadata))
                .build()
        )

        inputStream.close()
    }

    companion object : KLogging()
}