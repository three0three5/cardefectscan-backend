package ru.hse.cardefectscan.security.utils

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class BearerToken(
    private val bearerToken: String? = null,
    private val userId: Long? = null,
    authorities: List<GrantedAuthority>? = null,
) : AbstractAuthenticationToken(authorities) {
    override fun getCredentials(): String? {
        return bearerToken
    }

    override fun getPrincipal(): Long? {
        return userId
    }

    companion object {
        const val BEARER_PREFIX = "Bearer "
    }
}