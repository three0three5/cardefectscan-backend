package ru.hse.cardefectscan.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "_user")
class UserEntity(
    @Column(nullable = false, unique = true)
    var login: String,

    @Column(nullable = false)
    var hashedPassword: String,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "user")
    val refreshTokens: MutableList<RefreshTokenEntity> = mutableListOf(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {
}