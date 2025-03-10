package ru.hse.cardefectscan.service.security

import jakarta.transaction.Transactional
import mu.KLogging
import org.openapi.cardefectscan.model.LoginRequest
import org.openapi.cardefectscan.model.SignupRequest
import org.openapi.cardefectscan.model.TokenResponse
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.hse.cardefectscan.entity.UserEntity
import ru.hse.cardefectscan.exception.LoginOrPasswordIncorrectException
import ru.hse.cardefectscan.exception.UserExistsException
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
        try {
            userRepository.save(user)
        } catch (ex: DataIntegrityViolationException) {
            throw UserExistsException()
        }
        return tokenService.createAndPersistSession(user)
    }

    fun refresh(): TokenResponse {
        return tokenService.refreshSession()
    }

    fun login(loginRequest: LoginRequest): TokenResponse {
        logger.info { "login request from: ${loginRequest.username}" }
        val user = userRepository.findByLogin(loginRequest.username)
            ?: throw LoginOrPasswordIncorrectException()
        if (!passwordEncoder.matches(loginRequest.password, user.hashedPassword))
            throw LoginOrPasswordIncorrectException()
        return tokenService.createAndPersistSession(user)
    }

    fun logout() {
        tokenService.clearSessions()
    }

    companion object : KLogging()
}