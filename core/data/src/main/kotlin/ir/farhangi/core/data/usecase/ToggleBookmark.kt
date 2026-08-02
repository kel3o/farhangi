package ir.farhangi.core.data.usecase

import ir.farhangi.core.data.repository.AuthRepository
import ir.farhangi.core.data.repository.BookRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ToggleBookmark @Inject constructor(
    private val authRepository: AuthRepository,
    private val bookRepository: BookRepository,
) {
    suspend operator fun invoke(bookId: String, page: Int) {
        val userId = authRepository.observeSession().first()?.userId ?: return
        bookRepository.toggleBookmark(userId, bookId, page)
    }
}
