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
import org.openapi.cardefectscan.model.ImageRequestElement
import java.time.Instant

@Entity
@Table(name = "image_request")
class ImageRequestEntity (
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    val user: UserEntity,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: ImageRequestElement.Status = ImageRequestElement.Status.IMAGE_LOADING,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    var description: String? = null,

    @Id
    val imageName: String,
)