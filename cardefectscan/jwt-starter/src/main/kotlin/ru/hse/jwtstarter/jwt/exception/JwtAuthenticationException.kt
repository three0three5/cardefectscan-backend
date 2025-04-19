package ru.hse.jwtstarter.jwt.exception

import org.springframework.security.core.AuthenticationException

class JwtAuthenticationException(
    cause: Throwable?,
    message: String? = null,
) : AuthenticationException(
    message,
    cause,
)