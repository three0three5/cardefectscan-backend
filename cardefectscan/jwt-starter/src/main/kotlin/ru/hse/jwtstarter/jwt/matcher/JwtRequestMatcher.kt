package ru.hse.jwtstarter.jwt.matcher

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.security.web.util.matcher.RequestMatcher

class JwtRequestMatcher : RequestMatcher {
    override fun matches(request: HttpServletRequest?): Boolean {
        return request?.getHeaders(HttpHeaders.AUTHORIZATION)?.asSequence()?.filter {
            it.startsWith(BEARER_PREFIX)
        }?.firstOrNull() != null
    }

    companion object {
        const val BEARER_PREFIX = "Bearer "
    }
}