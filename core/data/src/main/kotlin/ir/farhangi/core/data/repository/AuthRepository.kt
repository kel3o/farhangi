package ir.farhangi.core.data.repository

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.model.Session
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun sendOtp(phone: String): Result<Unit>
    suspend fun verifyOtp(phone: String, code: String): Result<Session>
    fun observeSession(): Flow<Session?>
    fun observeLastPhone(): Flow<String?>
    fun observeOnboardingCompleted(): Flow<Boolean>
    fun observeNotificationPromptCompleted(): Flow<Boolean>
    suspend fun completeOnboarding()
    suspend fun completeNotificationPrompt()
    suspend fun signOut(): Result<Unit>
}