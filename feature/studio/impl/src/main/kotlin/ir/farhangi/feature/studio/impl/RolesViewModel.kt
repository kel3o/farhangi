package ir.farhangi.feature.studio.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.StudioRepository
import ir.farhangi.core.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RolesViewModel @Inject constructor(
    private val studioRepository: StudioRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<RolesUiState>(RolesUiState.Loading)
    val uiState: StateFlow<RolesUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            when (val result = studioRepository.getStaff()) {
                is Result.Success -> _uiState.value = RolesUiState.Success(result.data)
                is Result.Error -> _uiState.value = RolesUiState.Error(result.exception.message ?: "خطا")
                Result.Loading -> Unit
            }
        }
    }

    fun updateRole(userId: String, role: UserRole) {
        viewModelScope.launch {
            studioRepository.updateStaffRole(userId, role)
            refresh()
        }
    }
}
