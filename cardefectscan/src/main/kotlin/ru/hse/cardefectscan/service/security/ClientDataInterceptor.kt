package ru.hse.cardefectscan.service.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KLogging
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class ClientDataInterceptor(
    private val clientDataService: ClientDataService
) : HandlerInterceptor {

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        val fingerprint = request.getHeader("X-Device-Fingerprint")
        if (fingerprint != null) {
            clientDataService.setFingerprint(fingerprint)
            logger.info("Fingerprint received: $fingerprint")
        }
        return true
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        clientDataService.clear()
    }

    companion object : KLogging()
}
