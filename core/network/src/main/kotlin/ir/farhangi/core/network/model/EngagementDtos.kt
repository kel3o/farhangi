package ir.farhangi.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContestDto(
    val id: String,
    val title: String,
    val summary: String,
    val category: String,
    val status: String,
    @SerialName("question_count") val questionCount: Int,
    @SerialName("participant_count") val participantCount: Int,
    @SerialName("related_book_id") val relatedBookId: String? = null,
    @SerialName("related_course_id") val relatedCourseId: String? = null,
    @SerialName("ends_at") val endsAt: String,
    @SerialName("user_score_percent") val userScorePercent: Int? = null,
    @SerialName("duration_seconds") val durationSeconds: Int = 180,
    @SerialName("points_per_correct") val pointsPerCorrect: Int = 10,
)

@Serializable
data class QuizQuestionDto(
    val id: String,
    val prompt: String,
    val options: List<String>,
    @SerialName("correct_index") val correctIndex: Int,
)

@Serializable
data class LeaderboardEntryDto(
    val rank: Int,
    @SerialName("user_id") val userId: String,
    @SerialName("display_name") val displayName: String,
    val points: Int,
)

@Serializable
data class PointsDto(
    val reading: Int,
    val courses: Int,
    val contests: Int,
    val magazine: Int,
)

@Serializable
data class TrophyDto(
    val id: String,
    val title: String,
    val period: String,
    val board: String,
    @SerialName("label") val weekOrMonthLabel: String,
    val rank: Int = 1,
)

@Serializable
data class OrgMessageDto(
    val id: String,
    @SerialName("from_name") val fromName: String,
    @SerialName("from_role") val fromRole: String,
    val title: String,
    val body: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("is_read") val isRead: Boolean,
    val recipient: String = "CULTURAL_DEPUTY",
    @SerialName("image_url") val imageUrl: String? = null,
)

@Serializable
data class NamedCountDto(
    val name: String,
    val count: Int,
)

@Serializable
data class PlatformReportDto(
    @SerialName("section_audience") val sectionAudience: List<NamedCountDto>,
    @SerialName("top_books") val topBooks: List<NamedCountDto>,
    @SerialName("top_courses") val topCourses: List<NamedCountDto>,
    @SerialName("top_articles") val topArticles: List<NamedCountDto>,
)

@Serializable
data class StaffMemberDto(
    val id: String,
    @SerialName("display_name") val displayName: String,
    val phone: String,
    val role: String,
)
