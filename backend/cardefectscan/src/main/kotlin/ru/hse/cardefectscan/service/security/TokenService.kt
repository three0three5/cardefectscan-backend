package ru.hse.cardefectscan.service.security

import org.openapi.cardefectscan.model.TokenResponse
import org.springframework.stereotype.Service
import ru.hse.cardefectscan.entity.RefreshTokenEntity
import ru.hse.cardefectscan.entity.UserEntity
import ru.hse.cardefectscan.properties.AuthProperties
import ru.hse.cardefectscan.repository.RefreshTokenRepository
import ru.hse.jwtstarter.jwt.model.Role
import ru.hse.jwtstarter.jwt.service.JwtService

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
            userAgent = clientDataService.userAgent(),
            fingerprint = clientDataService.fingerprint(),
            expiresIn = authProperties.refreshLifespan,
        )
        val jwt = jwtService.createJwt(user.id!!, listOf(Role.ROLE_USER.name))
        refreshTokenRepository.save(refresh)
        cookieService.addRefresh(refresh.refreshToken.toString())
        return TokenResponse(jwt)
    }

    fun refresh(): TokenResponse {
        TODO()
    }

    fun clearSessions() {
        TODO("Not yet implemented")
    }
}