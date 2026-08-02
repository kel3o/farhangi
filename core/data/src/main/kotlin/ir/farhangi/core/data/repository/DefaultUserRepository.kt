package ir.farhangi.core.data.repository

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.model.UserProfile
import ir.farhangi.core.network.gateway.AuthGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultUserRepository @Inject constructor(
    private val authGateway: AuthGateway,
) : UserRepository {
    override fun observeProfile(): Flow<UserProfile?> =
        authGateway.observeSession().map { session ->
            session?.toProfile()
        }

    override suspend fun getProfile(): Result<UserProfile> {
        val session = authGateway.observeSession().first()
        return if (session != null) {
            Result.Success(session.toProfile())
        } else {
            Result.Error(IllegalStateException("کاربر وارد نشده است"))
        }
    }

    private fun ir.farhangi.core.model.Session.toProfile(): UserProfile = UserProfile(
        id = userId,
        phone = phone,
        displayName = displayName.orEmpty().ifBlank { "کاربر فرهنگی" },
        booksRead = DEMO_BOOKS_READ,
        coursesCompleted = DEMO_COURSES_COMPLETED,
        readingStreakDays = DEMO_STREAK_DAYS,
    )

    companion object {
        private const val DEMO_BOOKS_READ = 3
        private const val DEMO_COURSES_COMPLETED = 1
        private const val DEMO_STREAK_DAYS = 5
    }
}