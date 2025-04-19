package ru.hse.cardefectscan.service.security

import org.springframework.stereotype.Service

@Service
class ClientDataService {
    private val fingerprintHolder = ThreadLocal<String?>()

    fun setFingerprint(fingerprint: String) {
        fingerprintHolder.set(fingerprint)
    }

    fun fingerprint(): String? {
        return fingerprintHolder.get()
    }

    fun clear() {
        fingerprintHolder.remove()
    }
}