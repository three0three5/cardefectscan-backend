package ru.hse.cardefectscan.service.image

import mu.KLogging
import org.springframework.stereotype.Service
import ru.hse.jwtstarter.jwt.properties.JwtProperties
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Service
class HashService(
    private val jwtProperties: JwtProperties,
) {
    fun generateHmacSHA256(data: String): String {
        val keySpec = SecretKeySpec(jwtProperties.privateKey.toByteArray(), ALGORITHM)
        val mac = Mac.getInstance(ALGORITHM)
        mac.init(keySpec)
        val hashBytes = mac.doFinal(data.toByteArray())
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes)
    }

    fun verifyHmacSHA256(data: String, expectedHash: String): Boolean {
        val computedHash = generateHmacSHA256(data)
        logger.info { "computed hash: $computedHash" }
        return computedHash == expectedHash
    }

    companion object : KLogging() {
        const val ALGORITHM = "HmacSHA256"
    }
}