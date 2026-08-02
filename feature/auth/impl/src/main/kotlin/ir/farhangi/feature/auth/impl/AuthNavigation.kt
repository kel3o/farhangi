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
import ir.farhangi.feature.auth.api.OtpRoute
import ir.farhangi.feature.auth.api.PhoneRoute
import ir.farhangi.feature.home.api.HomeRoute

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
                navigator.enterMain(HomeRoute)
            }
        }
        OtpScreen(
            phone = key.phone,
            uiState = uiState,
            onVerify = { code -> viewModel.verifyOtp(key.phone, code) },
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