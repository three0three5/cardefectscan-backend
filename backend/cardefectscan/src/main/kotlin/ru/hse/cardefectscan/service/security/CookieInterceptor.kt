package ru.hse.cardefectscan.service.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KLogging
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class CookieInterceptor(
    private val cookieService: CookieService,
) : HandlerInterceptor {
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        val refreshToken = request.cookies?.find { it.name == "refresh_token" }?.value
        if (refreshToken != null) {
            cookieService.setRefresh(refreshToken)
            logger.info("Extracted refresh token from cookie")
        }
        return true
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        cookieService.clear()
    }

    companion object : KLogging()
}