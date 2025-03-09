package ru.hse.cardefectscan.service.security

import org.springframework.stereotype.Service

@Service
class CookieService {
    private val refreshTokenHolder = ThreadLocal<String?>()

    fun setRefresh(refresh: String) {
        refreshTokenHolder.set(refresh)
    }

    fun retrieveRefresh(): String? {
        return refreshTokenHolder.get()
    }

    fun clear() {
        refreshTokenHolder.remove()
    }
}