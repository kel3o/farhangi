package ir.farhangi.feature.magazine.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.MagazineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    private val magazineRepository: MagazineRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ArticleDetailUiState>(ArticleDetailUiState.Loading)
    val uiState: StateFlow<ArticleDetailUiState> = _uiState.asStateFlow()

    fun load(articleId: String) {
        viewModelScope.launch {
            _uiState.value = ArticleDetailUiState.Loading
            when (val result = magazineRepository.getArticle(articleId)) {
                is Result.Success -> _uiState.value = ArticleDetailUiState.Success(result.data)
                is Result.Error -> _uiState.value =
                    ArticleDetailUiState.Error(result.exception.message ?: "خطا")
                Result.Loading -> Unit
            }
        }
    }
}