package ir.farhangi.core.data.usecase

import ir.farhangi.core.data.repository.AuthRepository
import ir.farhangi.core.data.repository.BookRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UpdateReadingProgress @Inject constructor(
    private val authRepository: AuthRepository,
    private val bookRepository: BookRepository,
) {
    suspend operator fun invoke(bookId: String, page: Int, totalPages: Int) {
        val userId = authRepository.observeSession().first()?.userId ?: return
        bookRepository.updateProgress(userId, bookId, page, totalPages)
    }
}
