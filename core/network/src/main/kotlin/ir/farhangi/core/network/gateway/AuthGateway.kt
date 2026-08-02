package ir.farhangi.core.network.gateway

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.model.Session
import kotlinx.coroutines.flow.Flow

/**
 * Backend-agnostic auth contract.
 * Current adapter: demo/fake or Supabase — swappable without touching UI/Domain.
 */
interface AuthGateway {
    suspend fun sendOtp(phone: String): Result<Unit>
    suspend fun verifyOtp(phone: String, code: String): Result<Session>
    fun observeSession(): Flow<Session?>
    suspend fun signOut(): Result<Unit>
}