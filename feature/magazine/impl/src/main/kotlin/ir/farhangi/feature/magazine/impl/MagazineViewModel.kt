package ir.farhangi.feature.magazine.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.MagazineRepository
import ir.farhangi.core.model.Article
import ir.farhangi.core.model.MediaType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MagazineViewModel @Inject constructor(
    private val magazineRepository: MagazineRepository,
) : ViewModel() {

    private val selectedType = MutableStateFlow<MediaType?>(null)
    private val articlesResult = MutableStateFlow<Result<List<Article>>?>(null)

    init {
        viewModelScope.launch {
            articlesResult.value = magazineRepository.getArticles()
        }
    }

    val uiState: StateFlow<MagazineUiState> = combine(
        articlesResult.filterNotNull(),
        selectedType,
    ) { result, type ->
        when (result) {
            is Result.Success -> {
                val all = result.data
                val types = all.map { it.type }.distinct().sortedBy { it.name }
                MagazineUiState.Success(
                    articles = all.filterByType(type),
                    selectedType = type,
                    availableTypes = types,
                )
            }
            is Result.Error -> MagazineUiState.Error(result.exception.message ?: "خطا")
            Result.Loading -> MagazineUiState.Loading
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), MagazineUiState.Loading)

    fun selectType(type: MediaType?) {
        selectedType.value = type
    }

    private fun List<Article>.filterByType(type: MediaType?): List<Article> =
        if (type == null) this else filter { it.type == type }

    companion object {
        private const val STOP_TIMEOUT_MS = 5_000L
    }
}
