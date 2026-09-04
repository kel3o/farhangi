package ir.farhangi.feature.competitions.impl

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
import ir.farhangi.feature.books.api.BookDetailRoute
import ir.farhangi.feature.competitions.api.CompetitionsRoute
import ir.farhangi.feature.competitions.api.ContestDetailRoute
import ir.farhangi.feature.competitions.api.QuizRoute

@Module
@InstallIn(ActivityRetainedComponent::class)
object CompetitionsNavigationModule {
    @Provides
    @IntoSet
    fun provideCompetitionsEntryInstaller(navigator: Navigator): EntryProviderInstaller = {
        competitionsEntries(navigator)
    }
}

fun EntryProviderScope<NavKey>.competitionsEntries(navigator: Navigator) {
    entry<CompetitionsRoute> {
        val viewModel: CompetitionsViewModel = hiltViewModel()
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        CompetitionsScreen(
            uiState = uiState,
            onContestClick = { navigator.navigate(ContestDetailRoute(it.id)) },
            onCategorySelected = viewModel::selectCategory,
            onStatusSelected = viewModel::selectStatus,
        )
    }
    entry<ContestDetailRoute> { key ->
        val viewModel: ContestDetailViewModel = hiltViewModel()
        LaunchedEffect(key.contestId) { viewModel.load(key.contestId) }
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        ContestDetailScreen(
            uiState = uiState,
            onStartQuiz = { navigator.navigate(QuizRoute(key.contestId)) },
            onOpenBookSource = { bookId -> navigator.navigate(BookDetailRoute(bookId)) },
            onBack = { navigator.pop() },
        )
    }
    entry<QuizRoute> { key ->
        val viewModel: QuizViewModel = hiltViewModel()
        LaunchedEffect(key.contestId) { viewModel.load(key.contestId) }
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        QuizScreen(
            uiState = uiState,
            onSelectOption = viewModel::selectOption,
            onNext = viewModel::next,
            onSubmit = viewModel::submit,
            onBack = { navigator.popToRoot() },
        )
    }
}
