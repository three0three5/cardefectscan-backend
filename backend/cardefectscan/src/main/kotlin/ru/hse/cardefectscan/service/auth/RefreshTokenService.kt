package ru.hse.cardefectscan.service.auth

import org.springframework.stereotype.Service
import ru.hse.cardefectscan.repository.RefreshTokenRepository

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository
) {
}