package ru.hse.cardefectscan.service.security

import org.springframework.stereotype.Service

@Service
class ClientDataService { // сервис для fingerprint, useragent и т д
    fun userAgent() = "userAgent" // TODO
    fun fingerprint() = "fingerprint" // TODO
}