package ir.farhangi.feature.magazine.impl

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
import ir.farhangi.feature.magazine.api.ArticleDetailRoute
import ir.farhangi.feature.magazine.api.MagazineRoute

@Module
@InstallIn(ActivityRetainedComponent::class)
object MagazineNavigationModule {
    @Provides
    @IntoSet
    fun provideMagazineEntryInstaller(navigator: Navigator): EntryProviderInstaller = {
        magazineEntries(navigator)
    }
}

fun EntryProviderScope<NavKey>.magazineEntries(navigator: Navigator) {
    entry<MagazineRoute> {
        val viewModel: MagazineViewModel = hiltViewModel()
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        MagazineScreen(
            uiState = uiState,
            onArticleClick = { navigator.navigate(ArticleDetailRoute(it.id)) },
            onTypeSelected = viewModel::selectType,
        )
    }
    entry<ArticleDetailRoute> { key ->
        val viewModel: ArticleDetailViewModel = hiltViewModel()
        LaunchedEffect(key.articleId) { viewModel.load(key.articleId) }
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        ArticleDetailScreen(uiState = uiState, onBack = { navigator.pop() })
    }
}