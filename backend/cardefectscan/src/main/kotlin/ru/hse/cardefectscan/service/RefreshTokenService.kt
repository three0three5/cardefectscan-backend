package ru.hse.cardefectscan.service

import org.springframework.stereotype.Service
import ru.hse.cardefectscan.repository.RefreshTokenRepository

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository
) {
}