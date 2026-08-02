package ir.farhangi.core.data.usecase

import ir.farhangi.core.data.repository.AuthRepository
import ir.farhangi.core.data.repository.BookRepository
import ir.farhangi.core.model.Highlight
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * MVP highlight: stores a short note for the current page.
 * Rich text selection UI is deferred to phase 2.
 */
class AddPageHighlight @Inject constructor(
    private val authRepository: AuthRepository,
    private val bookRepository: BookRepository,
) {
    suspend operator fun invoke(bookId: String, page: Int, text: String): Highlight? {
        val userId = authRepository.observeSession().first()?.userId ?: return null
        if (text.isBlank()) return null
        return bookRepository.addHighlight(userId, bookId, page, text.trim())
    }
}
