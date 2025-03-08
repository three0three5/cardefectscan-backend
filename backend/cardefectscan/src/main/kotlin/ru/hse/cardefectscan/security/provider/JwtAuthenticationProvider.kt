package ru.hse.cardefectscan.security.provider

import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import ru.hse.cardefectscan.security.exception.JwtAuthenticationException
import ru.hse.cardefectscan.security.service.JwtService
import ru.hse.cardefectscan.security.utils.BearerToken

class JwtAuthenticationProvider(
    private val jwtService: JwtService,
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication {
        authentication as BearerToken
        lateinit var decodedJwt: DecodedJWT
        try {
            decodedJwt = jwtService.verify(authentication.credentials)
        } catch (e: JWTVerificationException) {
            throw JwtAuthenticationException(e)
        }
        val claims = jwtService.getClaims(decodedJwt)
        return BearerToken(
            null,
            claims.userId,
            claims.roles,
        )
    }

    override fun supports(authentication: Class<*>?): Boolean {
        return authentication == BearerToken::class.java
    }
}