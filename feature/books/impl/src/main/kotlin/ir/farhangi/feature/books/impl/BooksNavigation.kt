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
import ir.farhangi.feature.books.api.BookContestsRoute
import ir.farhangi.feature.books.api.BookDetailRoute
import ir.farhangi.feature.books.api.BookReaderRoute
import ir.farhangi.feature.books.api.BooksRoute
import ir.farhangi.feature.books.api.HamkhanRoute
import ir.farhangi.feature.books.api.LibraryRoute
import ir.farhangi.feature.books.api.MyLibraryRoute
import ir.farhangi.feature.competitions.api.ContestDetailRoute

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
        BooksScreen(
            onLibraryClick = { navigator.navigate(LibraryRoute) },
            onMyLibraryClick = { navigator.navigate(MyLibraryRoute) },
            onContestsClick = { navigator.navigate(BookContestsRoute) },
            onHamkhanClick = { navigator.navigate(HamkhanRoute) },
        )
    }
    entry<LibraryRoute> {
        val viewModel: LibraryViewModel = hiltViewModel()
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        LibraryScreen(
            uiState = uiState,
            onBookClick = { book ->
                navigator.navigate(
                    BookDetailRoute(
                        bookId = book.id,
                        categoryLabel = book.categories.firstOrNull().orEmpty(),
                    ),
                )
            },
            onCategorySelected = viewModel::selectCategory,
        )
    }
    entry<MyLibraryRoute> {
        val viewModel: MyLibraryViewModel = hiltViewModel()
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        LibraryScreen(
            uiState = uiState,
            onBookClick = { book ->
                navigator.navigate(
                    BookDetailRoute(
                        bookId = book.id,
                        categoryLabel = book.categories.firstOrNull().orEmpty(),
                    ),
                )
            },
            onCategorySelected = {},
        )
    }
    entry<HamkhanRoute> {
        val viewModel: HamkhanViewModel = hiltViewModel()
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        HamkhanScreen(
            uiState = uiState,
            onPeriodSelected = viewModel::selectPeriod,
            onBoardSelected = viewModel::selectBoard,
        )
    }
    entry<BookContestsRoute> {
        val viewModel: BookContestsViewModel = hiltViewModel()
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        BookContestsScreen(
            uiState = uiState,
            onContestClick = { navigator.navigate(ContestDetailRoute(it.id)) },
        )
    }
    entry<BookDetailRoute> { key ->
        val viewModel: BookDetailViewModel = hiltViewModel()
        LaunchedEffect(key.bookId) { viewModel.load(key.bookId) }
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        BookDetailScreen(
            uiState = uiState,
            onReadClick = {
                navigator.navigate(
                    BookReaderRoute(
                        bookId = key.bookId,
                        categoryLabel = key.categoryLabel,
                    ),
                )
            },
            onSaveClick = viewModel::toggleSaved,
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
            onJumpToPage = viewModel::jumpToPage,
            onToggleNight = viewModel::toggleNightMode,
            onToggleBookmark = viewModel::onToggleBookmark,
            onFontSizeChange = viewModel::setFontSizeSp,
            onBack = { navigator.pop() },
        )
    }
}
