package ru.hse.jwtstarter.jwt.model

data class JwtClaims(
    val userId: Long,
    val roles: List<Role>,
)