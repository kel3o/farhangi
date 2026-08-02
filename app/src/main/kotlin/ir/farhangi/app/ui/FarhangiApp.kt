package ir.farhangi.app.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import ir.farhangi.app.R
import ir.farhangi.app.navigation.TopLevelDestination
import ir.farhangi.core.designsystem.component.FarhangiTopAppBar
import ir.farhangi.core.navigation.EntryProviderInstaller
import ir.farhangi.core.navigation.Navigator
import ir.farhangi.feature.auth.api.OtpRoute
import ir.farhangi.feature.auth.api.PhoneRoute
import ir.farhangi.feature.home.api.HomeRoute
import ir.farhangi.feature.search.api.SearchRoute

@Composable
fun FarhangiApp(
    navigator: Navigator,
    entryProviderInstallers: Set<EntryProviderInstaller>,
    isAuthenticated: Boolean,
    modifier: Modifier = Modifier,
) {
    val topLevelRoutes = remember {
        TopLevelDestination.entries.map { it.route }.toSet()
    }

    LaunchedEffect(Unit) {
        navigator.configureMain(
            start = HomeRoute,
            topLevels = topLevelRoutes,
        )
    }

    LaunchedEffect(isAuthenticated) {
        val current = navigator.backStack.lastOrNull()
        when {
            !isAuthenticated && current !is PhoneRoute && current !is OtpRoute -> {
                navigator.replaceAll(PhoneRoute)
            }
            isAuthenticated && (navigator.isAuthMode || current is PhoneRoute || current is OtpRoute) -> {
                navigator.enterMain(HomeRoute)
            }
            navigator.backStack.isEmpty() -> {
                if (isAuthenticated) {
                    navigator.enterMain(HomeRoute)
                } else {
                    navigator.replaceAll(PhoneRoute)
                }
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

    // Read navigation state so Compose recomposes when tab/mode changes.
    val isAuthMode = navigator.isAuthMode
    val topLevelRoute = navigator.topLevelRoute
    val backStack = navigator.backStack

    if (!isAuthenticated || isAuthMode) {
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
