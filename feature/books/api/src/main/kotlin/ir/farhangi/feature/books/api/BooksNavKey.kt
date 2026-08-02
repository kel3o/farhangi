package ir.farhangi.feature.books.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object BooksRoute : NavKey

@Serializable
data class BookDetailRoute(val bookId: String) : NavKey

@Serializable
data class BookReaderRoute(val bookId: String) : NavKey