package ru.hse.cardefectscan.security.matcher

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.security.web.util.matcher.RequestMatcher
import ru.hse.cardefectscan.security.utils.BearerToken.Companion.BEARER_PREFIX

class JwtRequestMatcher : RequestMatcher {
    override fun matches(request: HttpServletRequest?): Boolean {
        return request?.getHeaders(HttpHeaders.AUTHORIZATION)?.asSequence()?.filter {
            it.startsWith(BEARER_PREFIX)
        }?.firstOrNull() != null
    }
}