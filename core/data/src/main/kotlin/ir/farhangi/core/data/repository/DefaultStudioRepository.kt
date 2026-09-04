package ir.farhangi.core.data.repository

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.common.result.map
import ir.farhangi.core.data.mapper.toDomain
import ir.farhangi.core.data.mapper.toEntity
import ir.farhangi.core.database.dao.OrgMessageDao
import ir.farhangi.core.model.Article
import ir.farhangi.core.model.Book
import ir.farhangi.core.model.Contest
import ir.farhangi.core.model.ContestReport
import ir.farhangi.core.model.Course
import ir.farhangi.core.model.OrgInboxRecipient
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
    private val orgMessageDao: OrgMessageDao,
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
            val lastOption = (question.options.size - 1).coerceAtLeast(0)
            QuizQuestionDto(
                id = question.id.ifBlank { "q$index" },
                prompt = question.prompt,
                options = question.options,
                correctIndex = question.correctIndex.coerceIn(0, lastOption),
            )
        }
        return studioGateway.upsertContest(contest.toDto(), questionDtos).map { it.toDomain() }
    }

    override suspend fun deleteBook(id: String): Result<Unit> = studioGateway.deleteBook(id)

    override suspend fun deleteCourse(id: String): Result<Unit> = studioGateway.deleteCourse(id)

    override suspend fun deleteArticle(id: String): Result<Unit> = studioGateway.deleteArticle(id)

    override suspend fun deleteContest(id: String): Result<Unit> = studioGateway.deleteContest(id)

    override suspend fun getContestReport(contestId: String): Result<ContestReport> =
        studioGateway.getContestReport(contestId).map { it.toDomain() }

    override suspend fun getOrgMessages(): Result<List<OrgMessage>> {
        return when (val remote = studioGateway.getOrgMessages()) {
            is Result.Success -> {
                orgMessageDao.upsertAll(remote.data.map { it.toEntity() })
                Result.Success(orgMessageDao.getAll().map { it.toDomain() })
            }
            is Result.Error -> {
                val local = orgMessageDao.getAll()
                if (local.isNotEmpty()) {
                    Result.Success(local.map { it.toDomain() })
                } else {
                    remote
                }
            }
            Result.Loading -> Result.Loading
        }
    }

    override suspend fun getOrgMessage(id: String): Result<OrgMessage> {
        val local = orgMessageDao.getById(id)
        if (local != null) return Result.Success(local.toDomain())
        return when (val messages = getOrgMessages()) {
            is Result.Success -> {
                val found = messages.data.firstOrNull { it.id == id }
                if (found != null) Result.Success(found)
                else Result.Error(NoSuchElementException("پیام یافت نشد"))
            }
            is Result.Error -> messages
            Result.Loading -> Result.Loading
        }
    }

    override suspend fun sendOrgMessage(
        title: String,
        body: String,
        recipient: OrgInboxRecipient,
    ): Result<OrgMessage> =
        studioGateway.sendOrgMessage(title, body, recipient.name).map { dto ->
            orgMessageDao.upsert(dto.toEntity())
            dto.toDomain()
        }

    override suspend fun markOrgMessageRead(id: String): Result<OrgMessage> {
        orgMessageDao.markRead(id)
        return when (val remote = studioGateway.markOrgMessageRead(id)) {
            is Result.Success -> {
                orgMessageDao.upsert(remote.data.toEntity())
                Result.Success(remote.data.toDomain())
            }
            is Result.Error -> {
                val local = orgMessageDao.getById(id)
                if (local != null) Result.Success(local.toDomain()) else remote
            }
            Result.Loading -> Result.Loading
        }
    }

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
    purchaseUrl = purchaseUrl,
)

private fun Course.toDto(): CourseDto = CourseDto(
    id = id,
    title = title,
    type = type.name,
    instructor = instructor,
    coverUrl = coverUrl,
    description = description,
    category = category,
    level = level,
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
    sourceUrl = sourceUrl,
)
