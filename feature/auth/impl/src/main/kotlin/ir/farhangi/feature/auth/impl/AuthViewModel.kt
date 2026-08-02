package ir.farhangi.feature.auth.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val lastPhone: StateFlow<String?> = authRepository.observeLastPhone()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), null)

    fun sendOtp(phone: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = authRepository.sendOtp(phone)) {
                is Result.Success -> _uiState.value = AuthUiState.OtpSent
                is Result.Error -> _uiState.value =
                    AuthUiState.Error(result.exception.message ?: "خطا در ارسال کد")
                Result.Loading -> Unit
            }
        }
    }

    fun verifyOtp(phone: String, code: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = authRepository.verifyOtp(phone, code)) {
                is Result.Success -> _uiState.value = AuthUiState.Authenticated
                is Result.Error -> _uiState.value =
                    AuthUiState.Error(result.exception.message ?: "کد نادرست است")
                Result.Loading -> Unit
            }
        }
    }

    fun clearError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Idle
        }
    }

    companion object {
        private const val STOP_TIMEOUT_MS = 5_000L
    }
}