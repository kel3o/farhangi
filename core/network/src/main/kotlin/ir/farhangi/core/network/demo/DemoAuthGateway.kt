package ir.farhangi.core.network.demo

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.model.Session
import ir.farhangi.core.model.UserRole
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
                displayName = displayNameFor(phone),
                role = roleFor(phone),
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
        const val DEMO_EDITOR_PHONE = "09111111111"
        const val DEMO_ORG_PHONE = "09222222222"
        const val DEMO_ADMIN_PHONE = "09333333333"
        private const val MIN_PHONE_DIGITS = 10
        private const val OTP_SEND_DELAY_MS = 400L
        private const val OTP_VERIFY_DELAY_MS = 400L
        private const val LAST_TEN_EDITOR = "9111111111"
        private const val LAST_TEN_ORG = "9222222222"
        private const val LAST_TEN_ADMIN = "9333333333"

        fun roleFor(phone: String): UserRole = when (phone.filter(Char::isDigit).takeLast(10)) {
            LAST_TEN_EDITOR -> UserRole.EDITOR
            LAST_TEN_ORG -> UserRole.ORGANIZATIONAL
            LAST_TEN_ADMIN -> UserRole.SUPER_ADMIN
            else -> UserRole.USER
        }

        private fun displayNameFor(phone: String): String = when (roleFor(phone)) {
            UserRole.EDITOR -> "ویرایشگر فرهنگی"
            UserRole.ORGANIZATIONAL -> "کاربر سازمانی"
            UserRole.SUPER_ADMIN -> "مدیرکل"
            UserRole.USER -> "کاربر فرهنگی"
        }
    }
}