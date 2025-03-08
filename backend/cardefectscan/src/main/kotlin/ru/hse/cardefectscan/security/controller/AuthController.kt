package ru.hse.cardefectscan.security.controller

import org.openapi.cardefectscan.api.AuthApi
import org.openapi.cardefectscan.model.LoginRequest
import org.openapi.cardefectscan.model.SignupRequest
import org.openapi.cardefectscan.model.TokenResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import ru.hse.cardefectscan.security.service.AuthService

@RestController
class AuthController(
    private val authService: AuthService,
) : AuthApi {
    override fun apiV1AuthSignupPost(signupRequest: SignupRequest): ResponseEntity<TokenResponse> {
        return ResponseEntity.ok(authService.signUp(signupRequest))
    }

    override fun apiV1AuthRefreshPost(): ResponseEntity<TokenResponse> {
        return ResponseEntity.ok(authService.refresh())
    }

    override fun apiV1AuthLoginPost(loginRequest: LoginRequest): ResponseEntity<TokenResponse> {
        return ResponseEntity.ok(authService.login(loginRequest))
    }

    override fun apiV1AuthLogoutPost(): ResponseEntity<Unit> {
        authService.logout()
        return ResponseEntity.ok().build()
    }
}