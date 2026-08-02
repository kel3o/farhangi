package ir.farhangi.feature.search.impl

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import ir.farhangi.core.model.SearchContentType
import ir.farhangi.core.navigation.EntryProviderInstaller
import ir.farhangi.core.navigation.Navigator
import ir.farhangi.feature.books.api.BookDetailRoute
import ir.farhangi.feature.courses.api.CourseDetailRoute
import ir.farhangi.feature.magazine.api.ArticleDetailRoute
import ir.farhangi.feature.search.api.SearchRoute

@Module
@InstallIn(ActivityRetainedComponent::class)
object SearchNavigationModule {
    @Provides
    @IntoSet
    fun provideSearchEntryInstaller(navigator: Navigator): EntryProviderInstaller = {
        searchEntries(navigator)
    }
}

fun EntryProviderScope<NavKey>.searchEntries(navigator: Navigator) {
    entry<SearchRoute> {
        val viewModel: SearchViewModel = hiltViewModel()
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        SearchScreen(
            uiState = uiState,
            onQueryChange = viewModel::onQueryChange,
            onTypeSelected = viewModel::onTypeSelected,
            onResultClick = { result ->
                when (result.type) {
                    SearchContentType.BOOK -> navigator.navigate(BookDetailRoute(result.id))
                    SearchContentType.COURSE -> navigator.navigate(CourseDetailRoute(result.id))
                    else -> navigator.navigate(ArticleDetailRoute(result.id))
                }
            },
            onBack = { navigator.pop() },
        )
    }
}