package ru.hse.cardefectscan.service.image

import io.minio.GetObjectArgs
import io.minio.GetObjectResponse
import io.minio.GetPresignedObjectUrlArgs
import io.minio.MinioClient
import io.minio.http.Method
import mu.KLogging
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import ru.hse.cardefectscan.exception.UnauthorizedException
import ru.hse.cardefectscan.properties.AppProperties
import ru.hse.cardefectscan.properties.MinioProperties
import ru.hse.cardefectscan.service.image.ImageName.Companion.LOADED_FOLDER
import java.net.URI

@Service
class LinkComposer(
    private val hashService: HashService,
    private val minioClient: MinioClient,
    private val minioProperties: MinioProperties,
    private val appProperties: AppProperties,
) {
    fun <T> withCheck(imageName: ImageName, hash: String, block: () -> T): T {
        logger.info { "checking imageName: $imageName" }
        if (!hashService.verifyHmacSHA256(imageName.toString(), hash)) throw UnauthorizedException("Hash check failed")
        return block.invoke()
    }

    fun thumbnail(imageName: String, userId: Long): String? {
        return proxiedLink(
            ImageName(
                filename = imageName,
                folderName = LOADED_FOLDER,
                userId = userId,
            )
        )
    }

    fun linkForPut(imageName: ImageName): String {
        logger.info { "Generating put link" }
        val host = URI.create(appProperties.host)
        val originalUrl = minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .bucket(minioProperties.bucket)
                .`object`(imageName.toString())
                .method(Method.PUT)
                .expiry(minioProperties.putLinkExpiration)
                .build()
        )

        logger.info { "original url: $originalUrl" }

        val originalUri = URI.create(originalUrl)

        val encodedQuery = originalUri.rawQuery ?: ""

        return UriComponentsBuilder.newInstance()
            .scheme(host.scheme)
            .host(host.host)
            .port(host.port)
            .path("$MINIO_PATH_PREFIX${originalUri.rawPath}")
            .query(encodedQuery)
            .build()
            .toUriString()
    }

    fun getObjectStream(imageName: ImageName): GetObjectResponse =
        minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(minioProperties.bucket)
                .`object`(imageName.toString())
                .build()
        )

    fun proxiedLink(imageName: ImageName): String =
        UriComponentsBuilder.fromUriString(appProperties.host)
            .path(IMAGE_DOWNLOAD_PATH)
            .pathSegment(imageName.folderName, imageName.filename)
            .queryParam(HASH_QUERY_PARAM_NAME, hashService.generateHmacSHA256(imageName.toString()))
            .build()
            .toUriString()

    companion object : KLogging() {
        const val IMAGE_DOWNLOAD_PATH = "/cardefectscan/api/v1/images"
        const val HASH_QUERY_PARAM_NAME = "hash"
        const val MINIO_PATH_PREFIX = "/minio"
    }
}