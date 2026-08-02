package ir.farhangi.core.data.repository

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.datastore.SessionLocalDataSource
import ir.farhangi.core.model.Session
import ir.farhangi.core.network.gateway.AuthGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultAuthRepository @Inject constructor(
    private val authGateway: AuthGateway,
    private val sessionLocalDataSource: SessionLocalDataSource,
) : AuthRepository {

    override suspend fun sendOtp(phone: String): Result<Unit> {
        val result = authGateway.sendOtp(phone)
        if (result is Result.Success) {
            sessionLocalDataSource.saveLastPhone(phone)
        }
        return result
    }

    override suspend fun verifyOtp(phone: String, code: String): Result<Session> {
        return when (val result = authGateway.verifyOtp(phone, code)) {
            is Result.Success -> {
                sessionLocalDataSource.saveSession(result.data)
                result
            }
            is Result.Error -> result
            Result.Loading -> result
        }
    }

    /**
     * Persisted session is the app source of truth across process death.
     * Gateway in-memory session is used only during the active OTP handshake.
     */
    override fun observeSession(): Flow<Session?> =
        sessionLocalDataSource.observeSession().distinctUntilChanged()

    override fun observeLastPhone(): Flow<String?> =
        sessionLocalDataSource.observeLastPhone()

    override suspend fun signOut(): Result<Unit> {
        val remote = authGateway.signOut()
        sessionLocalDataSource.clearSession()
        return remote
    }
}
