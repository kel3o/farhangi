package ir.farhangi.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val id: String,
    val title: String,
    val author: String,
    val publisher: String = "",
    val coverUrl: String? = null,
    val categories: List<String> = emptyList(),
    val totalPages: Int = 0,
    val rating: Double? = null,
    val description: String = "",
    val pdfUrl: String? = null,
    val pages: List<String> = emptyList(),
    val isSaved: Boolean = false,
    val purchaseUrl: String = DEFAULT_BOOK_PURCHASE_URL,
)

const val DEFAULT_BOOK_PURCHASE_URL =
    "https://taaghche.com/book/15842/%D8%A2%D8%A8-%D9%86%D8%A8%D8%A7%D8%AA-%D9%BE%D8%B3%D8%AA%D9%87-%D8%A7%DB%8C"