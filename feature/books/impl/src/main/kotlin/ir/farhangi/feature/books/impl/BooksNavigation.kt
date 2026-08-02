package ir.farhangi.feature.books.impl

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
import ir.farhangi.feature.books.api.BookReaderRoute
import ir.farhangi.feature.books.api.BooksRoute

@Module
@InstallIn(ActivityRetainedComponent::class)
object BooksNavigationModule {
    @Provides
    @IntoSet
    fun provideBooksEntryInstaller(navigator: Navigator): EntryProviderInstaller = {
        booksEntries(navigator)
    }
}

fun EntryProviderScope<NavKey>.booksEntries(navigator: Navigator) {
    entry<BooksRoute> {
        val viewModel: BooksViewModel = hiltViewModel()
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        BooksScreen(
            uiState = uiState,
            onBookClick = { navigator.navigate(BookDetailRoute(it.id)) },
        )
    }
    entry<BookDetailRoute> { key ->
        val viewModel: BookDetailViewModel = hiltViewModel()
        LaunchedEffect(key.bookId) { viewModel.load(key.bookId) }
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        BookDetailScreen(
            uiState = uiState,
            onReadClick = { navigator.navigate(BookReaderRoute(key.bookId)) },
            onBack = { navigator.pop() },
        )
    }
    entry<BookReaderRoute> { key ->
        val viewModel: BookReaderViewModel = hiltViewModel()
        LaunchedEffect(key.bookId) { viewModel.load(key.bookId) }
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        BookReaderScreen(
            uiState = uiState,
            onNext = viewModel::nextPage,
            onPrevious = viewModel::previousPage,
            onToggleNight = viewModel::toggleNightMode,
            onToggleBookmark = viewModel::onToggleBookmark,
            onAddHighlight = viewModel::onAddHighlight,
            onBack = { navigator.pop() },
        )
    }
}