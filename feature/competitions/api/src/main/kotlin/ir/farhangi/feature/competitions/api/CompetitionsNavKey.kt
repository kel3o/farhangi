package ir.farhangi.feature.competitions.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object CompetitionsRoute : NavKey

@Serializable
data class ContestDetailRoute(val contestId: String) : NavKey

@Serializable
data class QuizRoute(val contestId: String) : NavKey
