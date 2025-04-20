package ru.hse.cardefectscan.repository

import org.openapi.cardefectscan.model.ImageRequestStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import ru.hse.cardefectscan.entity.ImageRequestEntity
import ru.hse.cardefectscan.entity.UserEntity

interface ImageRequestRepository : JpaRepository<ImageRequestEntity, String> {
    fun findAllByUserAndStatusIn(
        user: UserEntity,
        statuses: List<ImageRequestStatus>,
        page: Pageable
    ): Page<ImageRequestEntity>

    fun findByImageNameAndUser(id: String, user: UserEntity): ImageRequestEntity?
}