package ru.hse.cardefectscan.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.hse.cardefectscan.entity.RefreshTokenEntity
import java.util.UUID

interface RefreshTokenRepository : JpaRepository<RefreshTokenEntity, Long> {
    fun findByRefreshToken(token: UUID): RefreshTokenEntity?
}