package ru.hse.cardefectscan.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.hse.cardefectscan.entity.UserEntity

interface UserRepository : JpaRepository<UserEntity, Long> {
}