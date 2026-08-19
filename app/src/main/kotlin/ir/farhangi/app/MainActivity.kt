package ir.farhangi.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import ir.farhangi.app.ui.FarhangiApp
import ir.farhangi.core.data.repository.AuthRepository
import ir.farhangi.core.data.repository.UserRepository
import ir.farhangi.core.designsystem.theme.FarhangiTheme
import ir.farhangi.core.navigation.EntryProviderInstaller
import ir.farhangi.core.navigation.Navigator
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var entryProviderInstallers: Set<@JvmSuppressWildcards EntryProviderInstaller>

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        setContent {
            FarhangiTheme(darkTheme = false) {
                val session by authRepository.observeSession()
                    .collectAsStateWithLifecycle(initialValue = null)
                val onboarding by authRepository.observeOnboardingCompleted()
                    .collectAsStateWithLifecycle(initialValue = false)
                val notification by authRepository.observeNotificationPromptCompleted()
                    .collectAsStateWithLifecycle(initialValue = false)
                val profile by userRepository.observeProfile()
                    .collectAsStateWithLifecycle(initialValue = null)
                FarhangiApp(
                    navigator = navigator,
                    entryProviderInstallers = entryProviderInstallers,
                    isAuthenticated = session != null,
                    hasCompletedOnboarding = onboarding,
                    hasCompletedNotificationPrompt = notification,
                    profileInitial = profile?.displayName?.take(1).orEmpty().ifBlank { "ک" },
                )
            }
        }
    }
}
