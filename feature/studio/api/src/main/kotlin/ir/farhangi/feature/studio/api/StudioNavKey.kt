package ir.farhangi.feature.studio.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object StudioHomeRoute : NavKey

@Serializable
data object StudioBooksRoute : NavKey

@Serializable
data class StudioBookEditorRoute(val bookId: String = "") : NavKey

@Serializable
data object StudioCoursesRoute : NavKey

@Serializable
data class StudioCourseEditorRoute(val courseId: String = "") : NavKey

@Serializable
data object StudioArticlesRoute : NavKey

@Serializable
data class StudioArticleEditorRoute(val articleId: String = "") : NavKey

@Serializable
data object StudioContestsRoute : NavKey

@Serializable
data class StudioContestEditorRoute(val contestId: String = "") : NavKey

@Serializable
data class StudioContestStatsRoute(val contestId: String) : NavKey

@Serializable
data object OrgInboxRoute : NavKey

@Serializable
data class OrgMessageDetailRoute(val messageId: String) : NavKey

@Serializable
data object ReportsRoute : NavKey

@Serializable
data object RolesRoute : NavKey
