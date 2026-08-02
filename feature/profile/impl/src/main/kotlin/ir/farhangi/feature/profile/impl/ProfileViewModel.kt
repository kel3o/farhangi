package ir.farhangi.feature.profile.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.data.repository.AuthRepository
import ir.farhangi.core.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    userRepository: UserRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    val uiState: StateFlow<ProfileUiState> = combine(
        userRepository.observeProfile(),
        authRepository.observeLastPhone(),
    ) { profile, lastPhone ->
        when {
            profile == null -> ProfileUiState.SignedOut
            else -> ProfileUiState.Success(profile = profile, lastPhone = lastPhone)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), ProfileUiState.Loading)

    fun signOut() {
        viewModelScope.launch { authRepository.signOut() }
    }

    companion object {
        private const val STOP_TIMEOUT_MS = 5_000L
    }
}
