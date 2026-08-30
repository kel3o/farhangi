package ir.farhangi.core.data.repository

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.database.dao.AudienceProfileDao
import ir.farhangi.core.database.entity.AudienceProfileEntity
import ir.farhangi.core.model.Gender
import ir.farhangi.core.model.Session
import ir.farhangi.core.model.UserProfile
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultUserRepository @Inject constructor(
    private val authRepository: AuthRepository,
    private val audienceProfileDao: AudienceProfileDao,
) : UserRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeProfile(): Flow<UserProfile?> =
        authRepository.observeSession().flatMapLatest { session ->
            if (session == null) {
                flowOf(null)
            } else {
                audienceProfileDao.observe(session.userId).map { audience ->
                    session.toProfile(audience)
                }
            }
        }.distinctUntilChanged()

    override suspend fun getProfile(): Result<UserProfile> {
        val session = authRepository.observeSession().first()
        return if (session != null) {
            val audience = audienceProfileDao.observe(session.userId).first()
            Result.Success(session.toProfile(audience))
        } else {
            Result.Error(IllegalStateException("کاربر وارد نشده است"))
        }
    }

    override suspend fun updateAudienceProfile(
        fullName: String,
        gender: Gender,
        age: Int,
    ): Result<Unit> {
        val session = authRepository.observeSession().first()
            ?: return Result.Error(IllegalStateException("کاربر وارد نشده است"))
        val trimmedName = fullName.trim()
        if (trimmedName.isBlank()) {
            return Result.Error(IllegalArgumentException("نام را وارد کنید"))
        }
        if (age !in AGE_MIN..AGE_MAX) {
            return Result.Error(IllegalArgumentException("سن معتبر نیست"))
        }
        audienceProfileDao.upsert(
            AudienceProfileEntity(
                userId = session.userId,
                fullName = trimmedName,
                gender = gender.name,
                age = age,
            ),
        )
        authRepository.updateDisplayName(trimmedName)
        return Result.Success(Unit)
    }

    private fun Session.toProfile(audience: AudienceProfileEntity?): UserProfile = UserProfile(
        id = userId,
        phone = phone,
        displayName = audience?.fullName?.ifBlank { null }
            ?: displayName.orEmpty().ifBlank { "کاربر فرهنگی" },
        gender = audience?.gender?.let { runCatching { Gender.valueOf(it) }.getOrNull() },
        age = audience?.age,
        role = role,
        booksRead = DEMO_BOOKS_READ,
        coursesCompleted = DEMO_COURSES_COMPLETED,
        readingStreakDays = DEMO_STREAK_DAYS,
    )

    companion object {
        private const val DEMO_BOOKS_READ = 3
        private const val DEMO_COURSES_COMPLETED = 1
        private const val DEMO_STREAK_DAYS = 5
        private const val AGE_MIN = 1
        private const val AGE_MAX = 120
    }
}
