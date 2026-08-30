package ir.farhangi.feature.profile.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.AuthRepository
import ir.farhangi.core.data.repository.EngagementRepository
import ir.farhangi.core.data.repository.UserRepository
import ir.farhangi.core.model.Gender
import ir.farhangi.core.model.PointsBreakdown
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val engagementRepository: EngagementRepository,
) : ViewModel() {
    val uiState: StateFlow<ProfileUiState> = combine(
        userRepository.observeProfile(),
        authRepository.observeLastPhone(),
        flow {
            val points = (engagementRepository.getPoints() as? Result.Success)?.data
                ?: PointsBreakdown(0, 0, 0, 0)
            val trophies = (engagementRepository.getTrophies() as? Result.Success)?.data.orEmpty()
            emit(points to trophies)
        },
    ) { profile, lastPhone, extras ->
        when {
            profile == null -> ProfileUiState.SignedOut
            else -> ProfileUiState.Success(
                profile = profile,
                lastPhone = lastPhone,
                points = extras.first,
                trophies = extras.second,
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), ProfileUiState.Loading)

    fun signOut() {
        viewModelScope.launch { authRepository.signOut() }
    }

    fun updateAudienceProfile(fullName: String, gender: Gender, age: Int) {
        viewModelScope.launch {
            userRepository.updateAudienceProfile(fullName, gender, age)
        }
    }

    companion object {
        private const val STOP_TIMEOUT_MS = 5_000L
    }
}
