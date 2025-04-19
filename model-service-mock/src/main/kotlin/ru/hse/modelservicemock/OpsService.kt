package ru.hse.modelservicemock

import org.springframework.stereotype.Service

@Service
class OpsService(
    var delay: Int = 10,
    var toFail: Boolean = false,
)