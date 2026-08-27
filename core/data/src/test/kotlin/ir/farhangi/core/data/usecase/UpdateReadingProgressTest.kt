package ir.farhangi.core.data.usecase

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.AuthRepository
import ir.farhangi.core.data.repository.BookRepository
import ir.farhangi.core.model.Book
import ir.farhangi.core.model.Bookmark
import ir.farhangi.core.model.Highlight
import ir.farhangi.core.model.ReadingProgress
import ir.farhangi.core.model.Session
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class UpdateReadingProgressTest {

    @Test
    fun invoke_updatesProgressForSignedInUser() = runTest {
        val bookRepo = FakeBookRepository()
        val authRepo = FakeAuthRepository(
            Session(
                userId = "u1",
                phone = "+98912",
                accessToken = "t",
            ),
        )
        val useCase = UpdateReadingProgress(authRepo, bookRepo)

        useCase(bookId = "book-1", page = 5, totalPages = 10)

        assertEquals(1, bookRepo.updates.size)
        assertEquals("book-1", bookRepo.updates.single().bookId)
        assertEquals(5, bookRepo.updates.single().page)
    }

    @Test
    fun invoke_skipsWhenSignedOut() = runTest {
        val bookRepo = FakeBookRepository()
        val useCase = UpdateReadingProgress(FakeAuthRepository(null), bookRepo)
        useCase("book-1", 2, 10)
        assertEquals(0, bookRepo.updates.size)
    }

    private class FakeAuthRepository(
        session: Session?,
    ) : AuthRepository {
        private val sessionFlow = MutableStateFlow(session)
        override suspend fun sendOtp(phone: String) = Result.Success(Unit)
        override suspend fun verifyOtp(phone: String, code: String) = Result.Error(IllegalStateException())
        override fun observeSession(): Flow<Session?> = sessionFlow
        override fun observeLastPhone(): Flow<String?> = flowOf(null)
        override fun observeOnboardingCompleted(): Flow<Boolean> = flowOf(true)
        override fun observeNotificationPromptCompleted(): Flow<Boolean> = flowOf(true)
        override suspend fun completeOnboarding() = Unit
        override suspend fun completeNotificationPrompt() = Unit
        override suspend fun signOut() = Result.Success(Unit)
    }

    private class FakeBookRepository : BookRepository {
        data class Update(val bookId: String, val page: Int)
        val updates = mutableListOf<Update>()

        override suspend fun getBooks(query: String?) = Result.Success(emptyList<Book>())
        override suspend fun getBook(id: String) = Result.Error(NoSuchElementException())
        override fun observeProgress(userId: String, bookId: String) = flowOf<ReadingProgress?>(null)
        override fun observeContinueReading(userId: String) = flowOf(emptyList<ReadingProgress>())
        override suspend fun updateProgress(userId: String, bookId: String, page: Int, totalPages: Int) {
            updates += Update(bookId, page)
        }
        override fun observeBookmark(userId: String, bookId: String, page: Int) = flowOf<Bookmark?>(null)
        override suspend fun toggleBookmark(
            userId: String,
            bookId: String,
            page: Int,
            bookTitle: String,
            note: String,
        ) = Unit
        override fun observeHighlights(userId: String, bookId: String, page: Int) = flowOf(emptyList<Highlight>())
        override suspend fun addHighlight(userId: String, bookId: String, page: Int, text: String) =
            Highlight("1", bookId, page, text, 0L)
        override suspend fun removeHighlight(highlightId: String) = Unit
    }
}
