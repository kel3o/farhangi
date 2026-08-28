package ir.farhangi.app.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import ir.farhangi.app.navigation.TopLevelDestination
import ir.farhangi.app.navigation.breadcrumbTitle
import ir.farhangi.core.designsystem.component.FarhangiTopAppBar
import ir.farhangi.core.navigation.EntryProviderInstaller
import ir.farhangi.core.navigation.Navigator
import ir.farhangi.feature.auth.api.NotificationPermissionRoute
import ir.farhangi.feature.auth.api.OnboardingRoute
import ir.farhangi.feature.auth.api.OtpRoute
import ir.farhangi.feature.auth.api.PhoneRoute
import ir.farhangi.feature.books.api.BooksRoute
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
    var showExitDialog by remember { mutableStateOf(false) }
    val activity = LocalContext.current as? Activity
    val isAuthMode = navigator.isAuthMode
    val topLevelRoute = navigator.topLevelRoute
    val backStack = navigator.backStack

    val atAppExitPoint = when {
        backStack.isEmpty() -> true
        isAuthMode -> backStack.size <= 1
        backStack.size > 1 -> false
        topLevelRoute != BooksRoute -> false
        else -> true
    }

    fun handleNavigateBack() {
        if (!navigator.pop()) {
            showExitDialog = true
        }
    }

    BackHandler(enabled = showExitDialog || atAppExitPoint) {
        if (showExitDialog) {
            showExitDialog = false
        } else {
            showExitDialog = true
        }
    }

    LaunchedEffect(isAuthenticated, hasCompletedOnboarding, hasCompletedNotificationPrompt) {
        navigator.configureMain(
            start = BooksRoute,
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
            navigator.backStack.isEmpty() -> navigator.enterMain(BooksRoute)
            navigator.isAuthMode || current is PhoneRoute || current is OtpRoute ||
                current is OnboardingRoute || current is NotificationPermissionRoute -> {
                navigator.enterMain(BooksRoute)
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

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = {
                Text(
                    text = "خروج از برنامه",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            },
            text = {
                Text(
                    text = "مطمئن هستید می‌خواهید از برنامه خارج شوید؟",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            },
            confirmButton = {
                TextButton(onClick = { activity?.finish() }) {
                    Text("خارج می‌شود")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("بازگشت")
                }
            },
        )
    }

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
            onBack = { handleNavigateBack() },
            entryProvider = provider,
            modifier = modifier.fillMaxSize(),
        )
        return
    }

    val selected = TopLevelDestination.entries.firstOrNull { it.route == topLevelRoute }
        ?: TopLevelDestination.BOOKS

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
                    title = breadcrumbTitle(backStack),
                    onSearchClick = { navigator.navigate(SearchRoute) },
                    profileInitial = profileInitial,
                    onProfileClick = { navigator.navigate(ProfileRoute) },
                )
            },
        ) { innerPadding ->
            NavDisplay(
                backStack = backStack,
                onBack = { handleNavigateBack() },
                entryProvider = provider,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
        }
    }
}
