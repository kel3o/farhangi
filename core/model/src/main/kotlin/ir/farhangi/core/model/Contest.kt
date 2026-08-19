package ir.farhangi.core.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
enum class ContestCategory {
    BOOK,
    LIFESTYLE,
    GENERAL_KNOWLEDGE,
    PRACTICAL_COURSE,
}

fun ContestCategory.persianLabel(): String = when (this) {
    ContestCategory.BOOK -> "کتاب"
    ContestCategory.LIFESTYLE -> "سبک زندگی"
    ContestCategory.GENERAL_KNOWLEDGE -> "اطلاعات عمومی"
    ContestCategory.PRACTICAL_COURSE -> "دوره‌های کاربردی"
}

@Serializable
enum class ContestStatus {
    LIVE,
    FINISHED,
}

fun ContestStatus.persianLabel(): String = when (this) {
    ContestStatus.LIVE -> "در جریان"
    ContestStatus.FINISHED -> "تمام‌شده"
}

@Serializable
data class Contest(
    val id: String,
    val title: String,
    val summary: String,
    val category: ContestCategory,
    val status: ContestStatus,
    val questionCount: Int,
    val participantCount: Int,
    val relatedBookId: String? = null,
    val relatedCourseId: String? = null,
    val endsAt: Instant,
    val userScorePercent: Int? = null,
)

@Serializable
data class QuizQuestion(
    val id: String,
    val prompt: String,
    val options: List<String>,
)

@Serializable
data class QuizSubmissionResult(
    val contestId: String,
    val correctCount: Int,
    val totalCount: Int,
    val percent: Int,
)
