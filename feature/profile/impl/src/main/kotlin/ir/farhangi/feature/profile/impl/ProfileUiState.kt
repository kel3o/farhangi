package ir.farhangi.feature.profile.impl

import ir.farhangi.core.model.UserProfile

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data object SignedOut : ProfileUiState
    data class Success(
        val profile: UserProfile,
        val lastPhone: String?,
    ) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}
