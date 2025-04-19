package ru.hse.cardefectscan.service.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import ru.hse.jwtstarter.jwt.model.JwtClaims

@Service
class AuthDetailsService {
    fun getCurrentUser(): JwtClaims {
        return SecurityContextHolder.getContext().authentication.principal as JwtClaims
    }
}