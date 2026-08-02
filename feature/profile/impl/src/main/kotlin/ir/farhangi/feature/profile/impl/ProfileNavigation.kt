package ir.farhangi.feature.profile.impl

import androidx.compose.runtime.LaunchedEffect
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
import ir.farhangi.feature.auth.api.PhoneRoute
import ir.farhangi.feature.profile.api.ProfileRoute

@Module
@InstallIn(ActivityRetainedComponent::class)
object ProfileNavigationModule {
    @Provides
    @IntoSet
    fun provideProfileEntryInstaller(navigator: Navigator): EntryProviderInstaller = {
        profileEntries(navigator)
    }
}

fun EntryProviderScope<NavKey>.profileEntries(navigator: Navigator) {
    entry<ProfileRoute> {
        val viewModel: ProfileViewModel = hiltViewModel()
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        LaunchedEffect(uiState) {
            if (uiState is ProfileUiState.SignedOut) {
                navigator.replaceAll(PhoneRoute)
            }
        }
        ProfileScreen(
            uiState = uiState,
            onSignOut = viewModel::signOut,
        )
    }
}