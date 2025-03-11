package ru.hse.cardefectscan.service.image

class ImageName(
    val filename: String,
    val userId: Long,
    val folderName: String,
) {
    override fun toString() = "$userId/$folderName/$filename"

    companion object {
        const val LOADED_FOLDER = "loaded"
        const val PROCESSED_FOLDER = "processed"

        fun fromStringWithBucket(value: String) = value.split("/").let {
            ImageName(
                userId = it[1].toLong(),
                folderName = it[2],
                filename = it[3]
            )
        }
    }
}