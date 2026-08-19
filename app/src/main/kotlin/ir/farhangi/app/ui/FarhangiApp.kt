package ir.farhangi.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import ir.farhangi.app.R
import ir.farhangi.app.navigation.TopLevelDestination
import ir.farhangi.core.designsystem.component.FarhangiTopAppBar
import ir.farhangi.core.navigation.EntryProviderInstaller
import ir.farhangi.core.navigation.Navigator
import ir.farhangi.feature.auth.api.NotificationPermissionRoute
import ir.farhangi.feature.auth.api.OnboardingRoute
import ir.farhangi.feature.auth.api.OtpRoute
import ir.farhangi.feature.auth.api.PhoneRoute
import ir.farhangi.feature.home.api.HomeRoute
import ir.farhangi.feature.profile.api.ProfileRoute
import ir.farhangi.feature.search.api.SearchRoute

@Composable
fun FarhangiApp(
    navigator: Navigator,
    entryProviderInstallers: Set<EntryProviderInstaller>,
    isAuthenticated: Boolean,
    hasCompletedOnboarding: Boolean,
    hasCompletedNotificationPrompt: Boolean,
    profileInitial: String,
    modifier: Modifier = Modifier,
) {
    val topLevelRoutes = remember {
        TopLevelDestination.entries.map { it.route }.toSet()
    }

    LaunchedEffect(isAuthenticated, hasCompletedOnboarding, hasCompletedNotificationPrompt) {
        navigator.configureMain(
            start = HomeRoute,
            topLevels = topLevelRoutes,
        )
        val current = navigator.backStack.lastOrNull()
        when {
            !hasCompletedOnboarding -> {
                if (current !is OnboardingRoute) navigator.replaceAll(OnboardingRoute)
            }
            !isAuthenticated -> {
                if (current !is PhoneRoute && current !is OtpRoute) {
                    navigator.replaceAll(PhoneRoute)
                }
            }
            !hasCompletedNotificationPrompt -> {
                if (current !is NotificationPermissionRoute) {
                    navigator.replaceAll(NotificationPermissionRoute)
                }
            }
            navigator.backStack.isEmpty() -> navigator.enterMain(HomeRoute)
            navigator.isAuthMode || current is PhoneRoute || current is OtpRoute ||
                current is OnboardingRoute || current is NotificationPermissionRoute -> {
                navigator.enterMain(HomeRoute)
            }
        }
    }

    val provider = remember(entryProviderInstallers) {
        entryProvider {
            entryProviderInstallers.forEach { installer ->
                installer()
            }
        }
    }

    val isAuthMode = navigator.isAuthMode
    val topLevelRoute = navigator.topLevelRoute
    val backStack = navigator.backStack

    if (backStack.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val inGate = !hasCompletedOnboarding || !isAuthenticated || !hasCompletedNotificationPrompt || isAuthMode
    if (inGate) {
        NavDisplay(
            backStack = backStack,
            onBack = { navigator.pop() },
            entryProvider = provider,
            modifier = modifier.fillMaxSize(),
        )
        return
    }

    val selected = TopLevelDestination.entries.firstOrNull { it.route == topLevelRoute }
        ?: TopLevelDestination.HOME

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            TopLevelDestination.entries.forEach { destination ->
                item(
                    selected = destination == selected,
                    onClick = { navigator.switchTopLevel(destination.route) },
                    icon = {
                        Icon(
                            imageVector = if (destination == selected) {
                                destination.selectedIcon
                            } else {
                                destination.unselectedIcon
                            },
                            contentDescription = stringResource(destination.contentDescriptionResId),
                        )
                    },
                    label = { Text(stringResource(destination.labelResId)) },
                )
            }
        },
        modifier = modifier,
    ) {
        Scaffold(
            topBar = {
                FarhangiTopAppBar(
                    title = stringResource(R.string.top_bar_title),
                    onSearchClick = { navigator.navigate(SearchRoute) },
                    profileInitial = profileInitial,
                    onProfileClick = { navigator.navigate(ProfileRoute) },
                )
            },
        ) { innerPadding ->
            NavDisplay(
                backStack = backStack,
                onBack = { navigator.pop() },
                entryProvider = provider,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
        }
    }
}
