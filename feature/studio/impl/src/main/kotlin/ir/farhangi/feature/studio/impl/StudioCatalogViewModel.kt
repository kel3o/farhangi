package ir.farhangi.feature.studio.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.BookRepository
import ir.farhangi.core.data.repository.CourseRepository
import ir.farhangi.core.data.repository.EngagementRepository
import ir.farhangi.core.data.repository.MagazineRepository
import ir.farhangi.core.data.repository.StudioRepository
import ir.farhangi.core.model.persianLabel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class StudioCatalogKind {
    BOOK,
    COURSE,
    ARTICLE,
    CONTEST,
}

@HiltViewModel
class StudioCatalogViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val courseRepository: CourseRepository,
    private val magazineRepository: MagazineRepository,
    private val engagementRepository: EngagementRepository,
    private val studioRepository: StudioRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<StudioListUiState>(StudioListUiState.Loading)
    val uiState: StateFlow<StudioListUiState> = _uiState.asStateFlow()

    fun refresh(kind: StudioCatalogKind) {
        viewModelScope.launch {
            _uiState.value = StudioListUiState.Loading
            _uiState.value = when (kind) {
                StudioCatalogKind.BOOK -> bookRepository.getBooks().toListState { book ->
                    StudioListItem(
                        id = book.id,
                        title = book.title,
                        subtitle = listOf(book.author, book.publisher).filter { it.isNotBlank() }
                            .joinToString(" · "),
                    )
                }
                StudioCatalogKind.COURSE -> courseRepository.getCourses().toListState { course ->
                    StudioListItem(
                        id = course.id,
                        title = course.title,
                        subtitle = listOf(course.instructor, course.category, course.type.persianLabel())
                            .filter { it.isNotBlank() }
                            .joinToString(" · "),
                    )
                }
                StudioCatalogKind.ARTICLE -> magazineRepository.getArticles().toListState { article ->
                    StudioListItem(
                        id = article.id,
                        title = article.title,
                        subtitle = article.category.persianLabel(),
                    )
                }
                StudioCatalogKind.CONTEST -> engagementRepository.getContests().toListState { contest ->
                    StudioListItem(
                        id = contest.id,
                        title = contest.title,
                        subtitle = "${contest.category.persianLabel()} · ${contest.status.persianLabel()}",
                    )
                }
            }
        }
    }

    fun delete(kind: StudioCatalogKind, id: String) {
        viewModelScope.launch {
            val result = when (kind) {
                StudioCatalogKind.BOOK -> studioRepository.deleteBook(id)
                StudioCatalogKind.COURSE -> studioRepository.deleteCourse(id)
                StudioCatalogKind.ARTICLE -> studioRepository.deleteArticle(id)
                StudioCatalogKind.CONTEST -> studioRepository.deleteContest(id)
            }
            if (result is Result.Success) {
                refresh(kind)
            }
        }
    }

    private fun <T> Result<List<T>>.toListState(
        mapItem: (T) -> StudioListItem,
    ): StudioListUiState = when (this) {
        is Result.Success -> StudioListUiState.Success(data.map(mapItem))
        is Result.Error -> StudioListUiState.Error(exception.message ?: "خطا")
        Result.Loading -> StudioListUiState.Loading
    }
}
