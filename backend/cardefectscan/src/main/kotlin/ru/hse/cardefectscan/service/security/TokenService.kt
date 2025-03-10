package ru.hse.cardefectscan.service.security

import mu.KLogging
import org.openapi.cardefectscan.model.TokenResponse
import org.springframework.stereotype.Service
import ru.hse.cardefectscan.entity.RefreshTokenEntity
import ru.hse.cardefectscan.entity.UserEntity
import ru.hse.cardefectscan.exception.SessionNotFoundException
import ru.hse.cardefectscan.properties.AuthProperties
import ru.hse.cardefectscan.repository.RefreshTokenRepository
import ru.hse.jwtstarter.jwt.model.Role
import ru.hse.jwtstarter.jwt.service.JwtService
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID

@Service
class TokenService(
    private val clientDataService: ClientDataService,
    private val authProperties: AuthProperties,
    private val jwtService: JwtService,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val cookieService: CookieService,
) {
    fun createAndPersistSession(user: UserEntity): TokenResponse {
        val refresh = RefreshTokenEntity(
            user = user,
            fingerprint = clientDataService.fingerprint(),
        )
        val jwt = jwtService.createJwt(user.id!!, listOf(Role.ROLE_USER.name))
        refreshTokenRepository.save(refresh)
        cookieService.setRefresh(refresh.refreshToken.toString())
        return TokenResponse(jwt)
    }

    fun refreshSession(): TokenResponse {
        val refresh = cookieService.retrieveRefresh() ?: throw SessionNotFoundException()
        val token = refreshTokenRepository.findByRefreshToken(UUID.fromString(refresh))
            ?: throw SessionNotFoundException()
        if ((token.createdAt + Duration.ofSeconds(authProperties.refreshLifespan)).isBefore(Instant.now())) {
            refreshTokenRepository.delete(token)
            throw SessionNotFoundException()
        }
        val newToken = RefreshTokenEntity(
            user = token.user,
            fingerprint = clientDataService.fingerprint(),
        )
        refreshTokenRepository.delete(token)
        refreshTokenRepository.save(newToken)
        val jwt = jwtService.createJwt(token.user.id!!, listOf(Role.ROLE_USER.name))
        cookieService.setRefresh(newToken.refreshToken.toString())
        return TokenResponse(jwt)
    }

    fun clearSessions() {
        val refresh = cookieService.retrieveRefresh() ?: return
        val token = refreshTokenRepository.findByRefreshToken(UUID.fromString(refresh)) ?: return
        val tokens = token.user.refreshTokens
        refreshTokenRepository.deleteAllInBatch(tokens)
    }

    companion object : KLogging()
}