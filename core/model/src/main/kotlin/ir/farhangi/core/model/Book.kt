package ir.farhangi.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val id: String,
    val title: String,
    val author: String,
    val coverUrl: String? = null,
    val categories: List<String> = emptyList(),
    val totalPages: Int = 0,
    val rating: Double? = null,
    val description: String = "",
)