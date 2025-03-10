package ru.hse.cardefectscan.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "image_request")
class ImageRequestEntity (
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    val user: UserEntity,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val status: ImageRequestStatus = ImageRequestStatus.IMAGE_LOADING,

    @Id
    val imageName: String,
)

enum class ImageRequestStatus {
    IMAGE_LOADING,
    IN_PROGRESS,
    DONE,
    FAILED,
}