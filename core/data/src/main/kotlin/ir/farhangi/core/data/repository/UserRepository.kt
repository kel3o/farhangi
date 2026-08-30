package ir.farhangi.core.data.repository

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.model.Gender
import ir.farhangi.core.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeProfile(): Flow<UserProfile?>
    suspend fun getProfile(): Result<UserProfile>
    suspend fun updateAudienceProfile(
        fullName: String,
        gender: Gender,
        age: Int,
    ): Result<Unit>
}