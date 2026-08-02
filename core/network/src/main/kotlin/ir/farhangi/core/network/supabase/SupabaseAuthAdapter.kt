package ir.farhangi.core.network.supabase

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.model.Session
import ir.farhangi.core.network.gateway.AuthGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Supabase Auth REST adapter (phone OTP).
 * UI/Domain never depend on this class — only [AuthGateway].
 */
@Singleton
class SupabaseAuthAdapter @Inject constructor(
    private val httpClient: SupabaseHttpClient,
) : AuthGateway {

    private val sessionState = MutableStateFlow<Session?>(null)
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun sendOtp(phone: String): Result<Unit> {
        return try {
            val body = json.encodeToString(OtpRequest(phone = phone))
            httpClient.post("/auth/v1/otp", body)
            Result.Success(Unit)
        } catch (error: Throwable) {
            Result.Error(error)
        }
    }

    override suspend fun verifyOtp(phone: String, code: String): Result<Session> {
        return try {
            val body = json.encodeToString(
                VerifyRequest(phone = phone, token = code, type = "sms"),
            )
            val response = httpClient.post("/auth/v1/verify", body)
            val parsed = json.decodeFromString<VerifyResponse>(response)
            val userId = parsed.user?.id
                ?: return Result.Error(IllegalStateException("user missing"))
            val session = Session(
                userId = userId,
                phone = phone,
                accessToken = parsed.accessToken,
                refreshToken = parsed.refreshToken,
                displayName = parsed.user.phone,
            )
            sessionState.value = session
            Result.Success(session)
        } catch (error: Throwable) {
            Result.Error(error)
        }
    }

    override fun observeSession(): Flow<Session?> = sessionState.asStateFlow()

    override suspend fun signOut(): Result<Unit> {
        sessionState.value = null
        return Result.Success(Unit)
    }

    @Serializable
    private data class OtpRequest(val phone: String)

    @Serializable
    private data class VerifyRequest(
        val phone: String,
        val token: String,
        val type: String,
    )

    @Serializable
    private data class VerifyResponse(
        @SerialName("access_token") val accessToken: String,
        @SerialName("refresh_token") val refreshToken: String? = null,
        val user: SupabaseUser? = null,
    )

    @Serializable
    private data class SupabaseUser(
        val id: String,
        val phone: String? = null,
    )
}
