package ru.hse.modelservicemock

import mu.KLogging
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class OpsController(
    private val opsService: OpsService,
) {
    @PatchMapping("/set_params")
    fun setParams(@RequestBody params: ParamsRequest) {
        params.delay?.let {
            opsService.delay = it
            logger.info { "set delay to $it" }
        }
        params.toFail?.let {
            opsService.toFail = it
            logger.info { "set fail to $it" }
        }
    }

    companion object : KLogging()
}

data class ParamsRequest(
    val delay: Int?,
    val toFail: Boolean?,
)