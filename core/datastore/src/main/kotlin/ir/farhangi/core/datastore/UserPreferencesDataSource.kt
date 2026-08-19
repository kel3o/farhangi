package ir.farhangi.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = UserPreferencesDataSource.DATA_STORE_NAME,
)

@Singleton
class UserPreferencesDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    val onboardingCompleted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.ONBOARDING_COMPLETED] == true
    }

    val lastPhone: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[Keys.LAST_PHONE]
    }

    val sessionJson: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[Keys.SESSION_JSON]
    }

    val notificationPromptCompleted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.NOTIFICATION_PROMPT_COMPLETED] == true
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDING_COMPLETED] = completed }
    }

    suspend fun setNotificationPromptCompleted(completed: Boolean) {
        context.dataStore.edit { it[Keys.NOTIFICATION_PROMPT_COMPLETED] = completed }
    }

    suspend fun setLastPhone(phone: String) {
        context.dataStore.edit { it[Keys.LAST_PHONE] = phone }
    }

    suspend fun saveSessionJson(json: String) {
        context.dataStore.edit { it[Keys.SESSION_JSON] = json }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.remove(Keys.SESSION_JSON) }
    }

    private object Keys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val NOTIFICATION_PROMPT_COMPLETED = booleanPreferencesKey("notification_prompt_completed")
        val LAST_PHONE = stringPreferencesKey("last_phone")
        val SESSION_JSON = stringPreferencesKey("session_json")
    }

    companion object {
        const val DATA_STORE_NAME = "farhangi_user_preferences"
    }
}
