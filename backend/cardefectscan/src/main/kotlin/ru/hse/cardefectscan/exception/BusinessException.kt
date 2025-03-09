package ru.hse.cardefectscan.exception

open class BusinessException(
    message: String? = null
) : RuntimeException(message)