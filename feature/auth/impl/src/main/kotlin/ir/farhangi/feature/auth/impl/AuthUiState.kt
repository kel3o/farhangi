package ir.farhangi.feature.auth.impl

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class Error(val message: String) : AuthUiState
    data object OtpSent : AuthUiState
    data object Authenticated : AuthUiState
}