package ir.farhangi.core.datastore

import ir.farhangi.core.model.Session
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionLocalDataSource @Inject constructor(
    private val preferences: UserPreferencesDataSource,
) {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun observeSession(): Flow<Session?> = preferences.sessionJson.map { encoded ->
        encoded?.let {
            runCatching { json.decodeFromString<Session>(it) }.getOrNull()
        }
    }

    fun observeLastPhone(): Flow<String?> = preferences.lastPhone

    fun observeOnboardingCompleted(): Flow<Boolean> = preferences.onboardingCompleted

    fun observeNotificationPromptCompleted(): Flow<Boolean> = preferences.notificationPromptCompleted

    suspend fun setOnboardingCompleted() {
        preferences.setOnboardingCompleted(true)
    }

    suspend fun setNotificationPromptCompleted() {
        preferences.setNotificationPromptCompleted(true)
    }

    suspend fun saveSession(session: Session) {
        preferences.saveSessionJson(json.encodeToString(session))
        preferences.setLastPhone(session.phone)
    }

    suspend fun saveLastPhone(phone: String) {
        preferences.setLastPhone(phone)
    }

    suspend fun clearSession() {
        preferences.clearSession()
    }
}
