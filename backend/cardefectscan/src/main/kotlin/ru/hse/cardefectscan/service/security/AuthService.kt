package ru.hse.cardefectscan.service.security

import jakarta.transaction.Transactional
import mu.KLogging
import org.openapi.cardefectscan.model.LoginRequest
import org.openapi.cardefectscan.model.SignupRequest
import org.openapi.cardefectscan.model.TokenResponse
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.hse.cardefectscan.entity.UserEntity
import ru.hse.cardefectscan.exception.LoginOrPasswordIncorrectException
import ru.hse.cardefectscan.repository.UserRepository

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenService: TokenService,
) {
    fun signUp(signupRequest: SignupRequest): TokenResponse {
        logger.info { "signup request from: ${signupRequest.username}" }
        val user = UserEntity(
            signupRequest.username,
            passwordEncoder.encode(signupRequest.password),
        )
        userRepository.save(user)
        return tokenService.createAndPersistSession(user)
    }

    fun refresh(): TokenResponse {
        return tokenService.refresh()
    }

    fun login(loginRequest: LoginRequest): TokenResponse {
        val user = userRepository.findByUsername(loginRequest.username)
            ?: throw LoginOrPasswordIncorrectException()
        if (!passwordEncoder.matches(user.hashedPassword, loginRequest.password))
            throw LoginOrPasswordIncorrectException()
        return tokenService.createAndPersistSession(user)
    }

    fun logout() {
        tokenService.clearSessions()
    }

    companion object : KLogging()
}