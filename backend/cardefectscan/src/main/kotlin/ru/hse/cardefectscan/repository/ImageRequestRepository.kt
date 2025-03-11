package ru.hse.cardefectscan.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.hse.cardefectscan.entity.ImageRequestEntity

interface ImageRequestRepository : JpaRepository<ImageRequestEntity, Long> {
}