package ir.farhangi.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ReadingProgress(
    val bookId: String,
    val page: Int,
    val percent: Float,
    val updatedAtEpochMs: Long,
)

@Serializable
data class Bookmark(
    val bookId: String,
    val page: Int,
    val note: String = "",
    val createdAtEpochMs: Long,
)

@Serializable
data class Highlight(
    val id: String,
    val bookId: String,
    val page: Int,
    val text: String,
    val createdAtEpochMs: Long,
)
