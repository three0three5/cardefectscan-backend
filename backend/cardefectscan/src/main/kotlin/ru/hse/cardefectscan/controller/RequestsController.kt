package ru.hse.cardefectscan.controller

import org.openapi.cardefectscan.api.RequestsApi
import org.openapi.cardefectscan.model.PageRequestResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import ru.hse.cardefectscan.service.requests.RequestsService

@RestController
class RequestsController(
    private val requestsService: RequestsService,
) : RequestsApi {
    override fun apiV1RequestsGet(page: Int, size: Int): ResponseEntity<PageRequestResponse> {
        return requestsService.getPaginatedRequests(page, size)
    }
}