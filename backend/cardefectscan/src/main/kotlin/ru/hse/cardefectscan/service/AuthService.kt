package ru.hse.cardefectscan.service

import mu.KLogging
import org.openapi.cardefectscan.model.LoginRequest
import org.openapi.cardefectscan.model.SignupRequest
import org.openapi.cardefectscan.model.TokenResponse
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val cookieService: CookieService,
    private val securityService: SecurityService,
    private val refreshTokenService: RefreshTokenService,
) {
    fun signUp(signupRequest: SignupRequest): TokenResponse {
        logger.info { "signup request from: ${signupRequest.username}" }
        TODO("Not yet implemented")
    }

    fun refresh(): TokenResponse {
        TODO("Not yet implemented")
    }

    fun login(loginRequest: LoginRequest): TokenResponse {
        TODO("Not yet implemented")
    }

    fun logout() {
        TODO("Not yet implemented")
    }

    companion object : KLogging()
}