package ru.hse.cardefectscan.service.security

import mu.KLogging
import org.openapi.cardefectscan.model.LoginRequest
import org.openapi.cardefectscan.model.SignupRequest
import org.openapi.cardefectscan.model.TokenResponse
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.hse.cardefectscan.entity.UserEntity
import ru.hse.cardefectscan.exception.LoginOrPasswordIncorrectException
import ru.hse.cardefectscan.exception.UserExistsException
import ru.hse.cardefectscan.properties.CookieProperties
import ru.hse.cardefectscan.repository.UserRepository
import java.time.Duration

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenService: TokenService,
    private val cookieService: CookieService,
    private val cookieProperties: CookieProperties,
) {
    fun signUp(signupRequest: SignupRequest): ResponseEntity<TokenResponse> {
        logger.info { "signup request from: ${signupRequest.username}" }
        val user = UserEntity(
            signupRequest.username,
            passwordEncoder.encode(signupRequest.password),
        )
        try {
            userRepository.save(user)
        } catch (ex: DataIntegrityViolationException) {
            throw UserExistsException()
        }
        return responseEntityWithCookie(user)
    }

    fun refresh(): ResponseEntity<TokenResponse> {
        val response = tokenService.refreshSession()
        val refresh = cookieService.retrieveRefresh()!!
        val cookie = getCookie(refresh)
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(response)
    }

    fun login(loginRequest: LoginRequest): ResponseEntity<TokenResponse> {
        logger.info { "login request from: ${loginRequest.username}" }
        val user = userRepository.findByLogin(loginRequest.username)
            ?: throw LoginOrPasswordIncorrectException()
        if (!passwordEncoder.matches(loginRequest.password, user.hashedPassword))
            throw LoginOrPasswordIncorrectException()
        return responseEntityWithCookie(user)
    }

    fun logout() {
        tokenService.clearSessions()
    }

    private fun responseEntityWithCookie(user: UserEntity): ResponseEntity<TokenResponse> {
        val body = tokenService.createAndPersistSession(user)
        val refresh = cookieService.retrieveRefresh()!!
        val cookie = getCookie(refresh)
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(body)
    }

    private fun getCookie(refresh: String) = ResponseCookie.from("refresh_token", refresh)
        .httpOnly(cookieProperties.isHttpOnly)
        .secure(cookieProperties.isSecure)
        .path("/api/v1/auth/")
        .maxAge(Duration.ofSeconds(cookieProperties.maxAge.toLong()))
        .build()

    companion object : KLogging()
}