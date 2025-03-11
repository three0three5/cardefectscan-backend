package ru.hse.cardefectscan.service.image

class ImageName(
    val filename: String,
    val userId: Long,
    val folderName: String,
) {
    override fun toString() = "$userId/$folderName/$filename"

    fun fromString(value: String) = value.split("/").let {
        ImageName(
            userId = it[0].toLong(),
            folderName = it[1],
            filename = it[2]
        )
    }

    fun fromStringWithBucket(value: String) = value.split("/").let {
        ImageName(
            userId = it[1].toLong(),
            folderName = it[2],
            filename = it[3]
        )
    }
}