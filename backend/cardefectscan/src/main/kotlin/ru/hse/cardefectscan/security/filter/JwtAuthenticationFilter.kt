package ru.hse.cardefectscan.security.filter

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.RequestMatcher
import ru.hse.cardefectscan.security.utils.BearerToken
import ru.hse.cardefectscan.security.utils.BearerToken.Companion.BEARER_PREFIX

class JwtAuthenticationFilter(
    requestMatcher: RequestMatcher,
    authenticationManager: AuthenticationManager,
) : AbstractAuthenticationProcessingFilter(requestMatcher, authenticationManager) {
    override fun attemptAuthentication(request: HttpServletRequest?, response: HttpServletResponse?): Authentication {
        val bearerToken = obtainToken(request!!) // matcher must've checked the request
        return authenticationManager.authenticate(BearerToken(bearerToken))
    }

    private fun obtainToken(request: HttpServletRequest): String {
        return request.getHeader(HttpHeaders.AUTHORIZATION).substringAfter(BEARER_PREFIX)
    }
}