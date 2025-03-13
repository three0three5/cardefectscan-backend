package ru.hse.cardefectscan.service.image

import io.minio.GetObjectArgs
import io.minio.GetObjectResponse
import io.minio.GetPresignedObjectUrlArgs
import io.minio.MinioClient
import io.minio.http.Method
import org.springframework.stereotype.Service
import ru.hse.cardefectscan.exception.UnauthorizedException
import ru.hse.cardefectscan.properties.MinioProperties

@Service
class LinkComposer(
    private val hashService: HashService,
    private val minioClient: MinioClient,
    private val minioProperties: MinioProperties,
) {
    fun <T> withCheck(imageId: String, hash: String, block: () -> T): T {
        if (!hashService.verifyHmacSHA256(imageId, hash)) throw UnauthorizedException()
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

    fun getObjectStream(imageName: String): GetObjectResponse =
        minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(minioProperties.bucket)
                .`object`(imageName)
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
}