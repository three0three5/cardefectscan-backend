package ru.hse.cardefectscan.service

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class TransactionHelper {
    @Transactional
    fun <T> launch(block: () -> T): T {
        return block.invoke()
    }

    @Transactional(propagation = Propagation.NESTED)
    fun <T> nested(block: () -> T): T {
        return block.invoke()
    }
}