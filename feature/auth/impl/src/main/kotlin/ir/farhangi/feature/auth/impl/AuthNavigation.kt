package ir.farhangi.feature.auth.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import ir.farhangi.core.navigation.EntryProviderInstaller
import ir.farhangi.core.navigation.Navigator
import ir.farhangi.feature.auth.api.NotificationPermissionRoute
import ir.farhangi.feature.auth.api.OnboardingRoute
import ir.farhangi.feature.auth.api.OtpRoute
import ir.farhangi.feature.auth.api.PhoneRoute

@Module
@InstallIn(ActivityRetainedComponent::class)
object AuthNavigationModule {

    @Provides
    @IntoSet
    fun provideAuthEntryInstaller(navigator: Navigator): EntryProviderInstaller = {
        authEntries(navigator)
    }
}

fun EntryProviderScope<NavKey>.authEntries(navigator: Navigator) {
    entry<OnboardingRoute> {
        val viewModel: AuthViewModel = hiltViewModel()
        OnboardingScreen(
            onFinished = {
                viewModel.completeOnboarding()
                navigator.replaceAll(PhoneRoute)
            },
        )
    }
    entry<PhoneRoute> {
        val viewModel: AuthViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val lastPhone by viewModel.lastPhone.collectAsStateWithLifecycle()
        PhoneRouteContent(
            uiState = uiState,
            initialPhone = lastPhone.orEmpty(),
            onSendOtp = viewModel::sendOtp,
            onOtpSent = { phone -> navigator.navigate(OtpRoute(phone)) },
        )
    }
    entry<OtpRoute> { key ->
        val viewModel: AuthViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        LaunchedEffect(uiState) {
            if (uiState is AuthUiState.Authenticated) {
                navigator.replaceAll(NotificationPermissionRoute)
            }
        }
        OtpScreen(
            phone = key.phone,
            uiState = uiState,
            onVerify = { code -> viewModel.verifyOtp(key.phone, code) },
        )
    }
    entry<NotificationPermissionRoute> {
        val viewModel: AuthViewModel = hiltViewModel()
        NotificationPermissionRouteContent(
            onCompleted = viewModel::completeNotificationPrompt,
        )
    }
}

@Composable
private fun PhoneRouteContent(
    uiState: AuthUiState,
    initialPhone: String,
    onSendOtp: (String) -> Unit,
    onOtpSent: (String) -> Unit,
) {
    var pendingPhone by remember { mutableStateOf("") }
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.OtpSent && pendingPhone.isNotBlank()) {
            onOtpSent(pendingPhone)
        }
    }
    PhoneScreen(
        uiState = uiState,
        initialPhone = initialPhone,
        onSendOtp = { phone ->
            pendingPhone = phone
            onSendOtp(phone)
        },
    )
}

@Composable
private fun NotificationPermissionRouteContent(
    onCompleted: () -> Unit,
) {
    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission(),
    ) {
        onCompleted()
    }
    NotificationPermissionScreen(
        onAllow = {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                onCompleted()
            }
        },
        onLater = onCompleted,
    )
}
