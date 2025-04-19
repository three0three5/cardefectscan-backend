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
import org.hibernate.annotations.UpdateTimestamp
import org.openapi.cardefectscan.model.ImageRequestStatus
import java.time.Instant

@Entity
@Table(name = "image_request")
class ImageRequestEntity(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    val user: UserEntity,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: ImageRequestStatus = ImageRequestStatus.IMAGE_LOADING,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(nullable = false)
    @UpdateTimestamp
    var updatedAt: Instant = Instant.now(),

    var description: String? = null,

    @Id
    val imageName: String,
)