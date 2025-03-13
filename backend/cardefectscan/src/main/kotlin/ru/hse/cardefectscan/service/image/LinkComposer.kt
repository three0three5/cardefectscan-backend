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

@Service
class LinkComposer(
    private val hashService: HashService,
    private val minioClient: MinioClient,
    private val minioProperties: MinioProperties,
    private val appProperties: AppProperties,
) {
    fun <T> withCheck(imageName: ImageName, hash: String, block: () -> T): T {
        if (!hashService.verifyHmacSHA256(imageName.toString(), hash)) throw UnauthorizedException()
        return block.invoke()
    }

    fun thumbnail(imageName: String): String? {
        return null // TODO
    }

    fun linkForPut(imageName: ImageName): String =
        minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .bucket(minioProperties.bucket)
                .`object`(imageName.toString())
                .method(Method.PUT)
                .expiry(minioProperties.putLinkExpiration)
                .build()
        )

    fun getObjectStream(imageName: ImageName): GetObjectResponse =
        minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(minioProperties.bucket)
                .`object`(imageName.toString())
                .build()
        )

    fun downloadLink(imageName: ImageName): String =
        minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(minioProperties.bucket)
                .`object`(imageName.toString())
                .expiry(minioProperties.getLinkExpiration)
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
        const val IMAGE_DOWNLOAD_PATH = "/api/v1/images"
        const val HASH_QUERY_PARAM_NAME = "hash"
        const val IMAGE_NAME_QUERY_PARAM_NAME = "image_id"
    }
}