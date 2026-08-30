package ir.farhangi.feature.studio.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object StudioHomeRoute : NavKey

@Serializable
data object CreateBookRoute : NavKey

@Serializable
data object CreateCourseRoute : NavKey

@Serializable
data object CreateArticleRoute : NavKey

@Serializable
data object CreateContestRoute : NavKey

@Serializable
data object OrgInboxRoute : NavKey

@Serializable
data class OrgMessageDetailRoute(val messageId: String) : NavKey

@Serializable
data object ReportsRoute : NavKey

@Serializable
data object RolesRoute : NavKey
