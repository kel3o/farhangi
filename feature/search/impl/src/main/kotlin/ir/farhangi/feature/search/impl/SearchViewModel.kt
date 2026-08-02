package ir.farhangi.feature.search.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.SearchRepository
import ir.farhangi.core.model.SearchContentType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    private var searchJob: Job? = null

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(DEBOUNCE_MS)
            performSearch()
        }
    }

    fun onTypeSelected(type: SearchContentType?) {
        _uiState.update { it.copy(selectedType = type) }
        viewModelScope.launch { performSearch() }
    }

    private suspend fun performSearch() {
        val state = _uiState.value
        if (state.query.isBlank()) {
            _uiState.update { it.copy(results = emptyList(), isLoading = false, errorMessage = null) }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        when (val result = searchRepository.search(state.query, state.selectedType)) {
            is Result.Success -> _uiState.update {
                it.copy(results = result.data, isLoading = false)
            }
            is Result.Error -> _uiState.update {
                it.copy(isLoading = false, errorMessage = result.exception.message ?: "خطا")
            }
            Result.Loading -> Unit
        }
    }

    companion object {
        private const val DEBOUNCE_MS = 300L
    }
}