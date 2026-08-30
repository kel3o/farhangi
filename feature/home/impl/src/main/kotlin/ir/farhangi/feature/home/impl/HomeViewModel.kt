package ir.farhangi.feature.home.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.usecase.GetHomeFeed
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeFeed: GetHomeFeed,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = flow {
        emit(HomeUiState.Loading)
        when (val result = getHomeFeed()) {
            is Result.Success -> {
                val feed = result.data
                emit(
                    HomeUiState.Success(
                        continueReading = feed.continueReading,
                        latestArticles = feed.latestArticles,
                        recommendedBooks = feed.recommendedBooks,
                        recentlyAdded = feed.recentlyAdded,
                        announcements = feed.announcements,
                        continueCourses = feed.continueCourses,
                        liveContests = feed.liveContests,
                        points = feed.points,
                        weeklyRank = feed.weeklyRank,
                        readingMinutesThisWeek = feed.readingMinutesThisWeek,
                        trophies = feed.trophies,
                    ),
                )
            }
            is Result.Error -> emit(HomeUiState.Error(result.exception.message ?: "بارگذاری خانه ناموفق بود"))
            Result.Loading -> Unit
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), HomeUiState.Loading)

    companion object {
        private const val STOP_TIMEOUT_MS = 5_000L
    }
}
