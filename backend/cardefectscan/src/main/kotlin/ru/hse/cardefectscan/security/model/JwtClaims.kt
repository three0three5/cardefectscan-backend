package ru.hse.cardefectscan.security.model

data class JwtClaims(
    val userId: Long,
    val roles: List<Role>,
)