package ru.hse.cardefectscan.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID


@Entity
@Table(name = "refresh_session")
class RefreshTokenEntity(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    val user: UserEntity,

    @Column(name = "refresh_token", unique = true, nullable = false)
    val refreshToken: UUID = UUID.randomUUID(),

    @Column(name = "user_agent", nullable = false)
    val userAgent: String,

    @Column(name = "fingerprint", nullable = false)
    val fingerprint: String,

    @Column(name = "expires_in", nullable = false)
    val expiresIn: Long,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {
}