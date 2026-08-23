package ir.farhangi.core.data.repository

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.common.result.map
import ir.farhangi.core.data.mapper.toDomain
import ir.farhangi.core.model.Article
import ir.farhangi.core.model.Book
import ir.farhangi.core.model.Contest
import ir.farhangi.core.model.Course
import ir.farhangi.core.model.OrgMessage
import ir.farhangi.core.model.PlatformReport
import ir.farhangi.core.model.QuizQuestion
import ir.farhangi.core.model.StaffMember
import ir.farhangi.core.model.UserRole
import ir.farhangi.core.network.gateway.StudioGateway
import ir.farhangi.core.network.model.ArticleDto
import ir.farhangi.core.network.model.BookDto
import ir.farhangi.core.network.model.ContestDto
import ir.farhangi.core.network.model.CourseDto
import ir.farhangi.core.network.model.CourseSectionDto
import ir.farhangi.core.network.model.QuizQuestionDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultStudioRepository @Inject constructor(
    private val studioGateway: StudioGateway,
) : StudioRepository {

    override suspend fun upsertBook(book: Book): Result<Book> =
        studioGateway.upsertBook(book.toDto()).map { it.toDomain() }

    override suspend fun upsertCourse(course: Course): Result<Course> =
        studioGateway.upsertCourse(course.toDto()).map { it.toDomain() }

    override suspend fun upsertArticle(article: Article): Result<Article> =
        studioGateway.upsertArticle(article.toDto()).map { it.toDomain() }

    override suspend fun upsertContest(
        contest: Contest,
        questions: List<QuizQuestion>,
    ): Result<Contest> {
        val questionDtos = questions.mapIndexed { index, question ->
            QuizQuestionDto(
                id = question.id.ifBlank { "q$index" },
                prompt = question.prompt,
                options = question.options,
                correctIndex = 0,
            )
        }
        return studioGateway.upsertContest(contest.toDto(), questionDtos).map { it.toDomain() }
    }

    override suspend fun getOrgMessages(): Result<List<OrgMessage>> =
        studioGateway.getOrgMessages().map { list -> list.map { it.toDomain() } }

    override suspend fun sendOrgMessage(title: String, body: String): Result<OrgMessage> =
        studioGateway.sendOrgMessage(title, body).map { it.toDomain() }

    override suspend fun getReport(): Result<PlatformReport> =
        studioGateway.getReport().map { it.toDomain() }

    override suspend fun getStaff(): Result<List<StaffMember>> =
        studioGateway.getStaff().map { list -> list.map { it.toDomain() } }

    override suspend fun updateStaffRole(userId: String, role: UserRole): Result<StaffMember> =
        studioGateway.updateStaffRole(userId, role.name).map { it.toDomain() }
}

private fun Book.toDto(): BookDto = BookDto(
    id = id,
    title = title,
    author = author,
    publisher = publisher,
    coverUrl = coverUrl,
    categories = categories,
    totalPages = totalPages.coerceAtLeast(pages.size),
    rating = rating,
    description = description,
    pdfUrl = pdfUrl,
    pages = pages,
)

private fun Course.toDto(): CourseDto = CourseDto(
    id = id,
    title = title,
    type = type.name,
    instructor = instructor,
    coverUrl = coverUrl,
    description = description,
    category = category,
    isFree = isFree,
    sections = sections.map { section ->
        CourseSectionDto(
            id = section.id,
            title = section.title,
            order = section.order,
            durationMinutes = section.durationMinutes,
            isCompleted = section.isCompleted,
            contentType = section.contentType.name,
            aparatUrl = section.aparatUrl,
            body = section.body,
        )
    },
    progress = progress,
)

private fun Article.toDto(): ArticleDto = ArticleDto(
    id = id,
    title = title,
    type = type.name,
    category = category.name,
    summary = summary,
    body = body,
    mediaUrl = mediaUrl,
    coverUrl = coverUrl,
    publishedAt = publishedAt.toString(),
)

private fun Contest.toDto(): ContestDto = ContestDto(
    id = id,
    title = title,
    summary = summary,
    category = category.name,
    status = status.name,
    questionCount = questionCount,
    participantCount = participantCount,
    relatedBookId = relatedBookId,
    relatedCourseId = relatedCourseId,
    endsAt = endsAt.toString(),
    userScorePercent = userScorePercent,
    durationSeconds = durationSeconds,
    pointsPerCorrect = pointsPerCorrect,
)
