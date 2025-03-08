package ru.hse.cardefectscan.security.exception

import org.springframework.security.core.AuthenticationException

class JwtAuthenticationException(
    cause: Throwable?,
    message: String? = null,
) : AuthenticationException(
    message,
    cause,
)