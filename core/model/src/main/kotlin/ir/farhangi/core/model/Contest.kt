package ir.farhangi.core.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
enum class ContestCategory {
    BOOK,
    LIFESTYLE,
    GENERAL_KNOWLEDGE,
    PRACTICAL_COURSE,
    MAGAZINE,
}

fun ContestCategory.persianLabel(): String = when (this) {
    ContestCategory.BOOK -> "کتاب"
    ContestCategory.LIFESTYLE -> "سبک زندگی"
    ContestCategory.GENERAL_KNOWLEDGE -> "اطلاعات عمومی"
    ContestCategory.PRACTICAL_COURSE -> "دوره‌های کاربردی"
    ContestCategory.MAGAZINE -> "نشریه"
}

fun ContestCategory.studioLabel(): String = when (this) {
    ContestCategory.BOOK -> "کتاب"
    ContestCategory.MAGAZINE -> "نشریه"
    ContestCategory.PRACTICAL_COURSE -> "آموزش"
    else -> persianLabel()
}

val STUDIO_CONTEST_CATEGORIES: List<ContestCategory> = listOf(
    ContestCategory.BOOK,
    ContestCategory.MAGAZINE,
    ContestCategory.PRACTICAL_COURSE,
)

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
    val durationSeconds: Int = DEFAULT_CONTEST_DURATION_SECONDS,
    val pointsPerCorrect: Int = DEFAULT_POINTS_PER_CORRECT,
    val sourceUrl: String? = null,
) {
    val hasSource: Boolean get() = !sourceUrl.isNullOrBlank()

    val sourceBookId: String?
        get() = sourceUrl
            ?.trim()
            ?.takeIf { it.startsWith(BOOK_SOURCE_ID_PREFIX) }
}

const val BOOK_SOURCE_ID_PREFIX = "book-"

const val DEFAULT_CONTEST_DURATION_SECONDS = 180
const val DEFAULT_POINTS_PER_CORRECT = 10

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
    val rank: Int? = null,
    val pointsAwarded: Int = 0,
    val endsAt: Instant? = null,
)
