package ru.hse.jwtstarter.jwt.filter

import com.auth0.jwt.exceptions.JWTVerificationException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KLogging
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.filter.OncePerRequestFilter
import ru.hse.jwtstarter.jwt.matcher.JwtRequestMatcher.Companion.BEARER_PREFIX
import ru.hse.jwtstarter.jwt.service.JwtService

class JwtAuthenticationFilter(
    private val requestMatcher: RequestMatcher,
    private val jwtService: JwtService,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (!requestMatcher.matches(request)) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val token = obtainToken(request) ?: run {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing token")
                return
            }

            val authentication = authenticate(token)
            SecurityContextHolder.getContext().authentication = authentication

            filterChain.doFilter(request, response)
        } catch (ex: JWTVerificationException) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token")
        } catch (ex: Exception) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal error")
        }
    }

    private fun obtainToken(request: HttpServletRequest): String? {
        return request.getHeader(HttpHeaders.AUTHORIZATION)?.removePrefix(BEARER_PREFIX)
    }

    private fun authenticate(token: String): Authentication {
        val decodedJWT = jwtService.verify(token)
        val claims = jwtService.getClaims(decodedJWT)
        return UsernamePasswordAuthenticationToken(claims, null, claims.roles)
    }

    companion object : KLogging()
}
