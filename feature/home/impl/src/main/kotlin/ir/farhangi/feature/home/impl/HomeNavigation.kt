package ir.farhangi.feature.home.impl

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
import ir.farhangi.feature.books.api.BookDetailRoute
import ir.farhangi.feature.competitions.api.ContestDetailRoute
import ir.farhangi.feature.courses.api.CourseDetailRoute
import ir.farhangi.feature.home.api.HomeRoute
import ir.farhangi.feature.magazine.api.ArticleDetailRoute

@Module
@InstallIn(ActivityRetainedComponent::class)
object HomeNavigationModule {
    @Provides
    @IntoSet
    fun provideHomeEntryInstaller(navigator: Navigator): EntryProviderInstaller = {
        homeEntries(navigator)
    }
}

fun EntryProviderScope<NavKey>.homeEntries(navigator: Navigator) {
    entry<HomeRoute> {
        val viewModel: HomeViewModel = hiltViewModel()
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        HomeScreen(
            uiState = uiState,
            onBookClick = {
                navigator.navigate(
                    BookDetailRoute(
                        bookId = it.id,
                        categoryLabel = it.categories.firstOrNull().orEmpty(),
                    ),
                )
            },
            onCourseClick = { navigator.navigate(CourseDetailRoute(it.id)) },
            onArticleClick = { navigator.navigate(ArticleDetailRoute(it.id)) },
            onContestClick = { navigator.navigate(ContestDetailRoute(it.id)) },
        )
    }
}