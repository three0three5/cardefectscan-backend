package ru.hse.modelservicemock

import com.fasterxml.jackson.databind.ObjectMapper
import io.minio.GetObjectArgs
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
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.time.Duration
import javax.imageio.ImageIO

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
            handle(imageProcessRequest)
        }
        return ResponseEntity.ok().build()
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
            info = "Не удалось найти автомобиль на изображении",
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
                mapOf(
                    "0" to ResultMetadata(ResultMetadata.DamageLevel.NONE, "empty"),
                    "1" to ResultMetadata(ResultMetadata.DamageLevel.SCRATCH, "left_headlight"),
                    "2" to ResultMetadata(ResultMetadata.DamageLevel.CRACK, "right_headlight"),
                    "3" to ResultMetadata(ResultMetadata.DamageLevel.DENT, "front_bumper"),
                )
        )
        val jsonMetadata = objectMapper.writeValueAsString(metadata)
        uploadImage(imageProcessRequest.downloadObjectName, imageProcessRequest.resultName, jsonMetadata)
        logger.info { "finished successfully" }
    }

    private fun uploadImage(imageName: String, resultName: String, jsonMetadata: String) {
        logger.info { "downloading image $imageName" }
        val imageBytes = minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(minioProperties.bucket)
                .`object`(imageName)
                .build()
        ).use { inputStream ->
            ByteArrayOutputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
                outputStream.toByteArray()
            }
        }
        val bufferedImage = ImageIO.read(imageBytes.inputStream())
        val imageToSave = processImage(bufferedImage)
        logger.info { "uploading image $resultName" }

        val imageToSaveBytes = ByteArrayOutputStream().use { outputStream ->
            ImageIO.write(imageToSave, "png", outputStream)
            outputStream.toByteArray()
        }

        val inputStream: InputStream = ByteArrayInputStream(imageToSaveBytes)

        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(minioProperties.bucket)
                .`object`(resultName)
                .stream(inputStream, imageToSaveBytes.size.toLong(), -1)
                .contentType("image/png")
                .userMetadata(mapOf("json-data" to jsonMetadata))
                .build()
        )
    }

    private fun processImage(bufferedImage: BufferedImage): BufferedImage {
        val width = bufferedImage.width
        val height = bufferedImage.height

        val processedImage = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)

        val midX = width / 2
        val midY = height / 2

        for (y in 0 until height) {
            for (x in 0 until width) {
                val value = when {
                    x < midX && y < midY -> 0
                    x >= midX && y < midY -> 1
                    x < midX && y >= midY -> 2
                    else -> 3
                }
                processedImage.raster.setSample(x, y, 0, value)
            }
        }

        return processedImage
    }

    companion object : KLogging()
}