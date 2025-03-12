package ru.hse.cardefectscan.service.image

class ImageName(
    val filename: String,
    val userId: Long,
    val folderName: String,
    val extension: String = "",
) {
    override fun toString() = "$userId/$folderName/$filename"

    companion object {
        const val LOADED_FOLDER = "loaded"
        const val PROCESSED_FOLDER = "processed"

        fun fromStringWithBucket(value: String) = value.split("/").let {
            val nameWithExtension = extractExtension(it[3])
            ImageName(
                userId = it[1].toLong(),
                folderName = it[2],
                filename = nameWithExtension.first,
                extension = nameWithExtension.second,
            )
        }

        private fun extractExtension(name: String) = name.split(".").let {
            if (it.size == 2) {
                Pair(it[0], it[1])
            } else {
                Pair(name, "")
            }
        }
    }
}