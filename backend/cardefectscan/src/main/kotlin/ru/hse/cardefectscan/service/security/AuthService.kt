package ru.hse.cardefectscan.service.security

import jakarta.transaction.Transactional
import mu.KLogging
import org.openapi.cardefectscan.model.LoginRequest
import org.openapi.cardefectscan.model.SignupRequest
import org.openapi.cardefectscan.model.TokenResponse
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.hse.cardefectscan.entity.UserEntity
import ru.hse.cardefectscan.repository.UserRepository

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenService: TokenService,
) {
    @Transactional
    fun signUp(signupRequest: SignupRequest): TokenResponse {
        logger.info { "signup request from: ${signupRequest.username}" }
        val user = UserEntity(
            signupRequest.username,
            passwordEncoder.encode(signupRequest.password),
        )
        userRepository.save(user)
        return tokenService.createAndPersist(user)
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