package ir.farhangi.core.network.demo

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.model.Session
import ir.farhangi.core.network.gateway.AuthGateway
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory demo auth for UI development without a real backend.
 * Accepts any phone; OTP code is always "123456".
 */
@Singleton
class DemoAuthGateway @Inject constructor() : AuthGateway {

    private val sessionState = MutableStateFlow<Session?>(null)

    override suspend fun sendOtp(phone: String): Result<Unit> {
        delay(OTP_SEND_DELAY_MS)
        return if (phone.filter(Char::isDigit).length >= MIN_PHONE_DIGITS) {
            Result.Success(Unit)
        } else {
            Result.Error(IllegalArgumentException("شماره موبایل نامعتبر است"))
        }
    }

    override suspend fun verifyOtp(phone: String, code: String): Result<Session> {
        delay(OTP_VERIFY_DELAY_MS)
        return if (code == DEMO_OTP_CODE) {
            val session = Session(
                userId = "demo-user",
                phone = phone,
                accessToken = "demo-token",
                displayName = "کاربر فرهنگی",
            )
            sessionState.value = session
            Result.Success(session)
        } else {
            Result.Error(IllegalArgumentException("کد تأیید نادرست است"))
        }
    }

    override fun observeSession(): Flow<Session?> = sessionState.asStateFlow()

    override suspend fun signOut(): Result<Unit> {
        sessionState.value = null
        return Result.Success(Unit)
    }

    companion object {
        const val DEMO_OTP_CODE = "123456"
        private const val MIN_PHONE_DIGITS = 10
        private const val OTP_SEND_DELAY_MS = 400L
        private const val OTP_VERIFY_DELAY_MS = 400L
    }
}