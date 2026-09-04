package ir.farhangi.core.network.demo

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.network.gateway.StudioGateway
import ir.farhangi.core.network.model.ArticleDto
import ir.farhangi.core.network.model.BookDto
import ir.farhangi.core.network.model.ContestDto
import ir.farhangi.core.network.model.ContestReportDto
import ir.farhangi.core.network.model.CourseDto
import ir.farhangi.core.network.model.OrgMessageDto
import ir.farhangi.core.network.model.PlatformReportDto
import ir.farhangi.core.network.model.QuizQuestionDto
import ir.farhangi.core.network.model.StaffMemberDto
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DemoStudioGateway @Inject constructor(
    private val store: DemoPlatformStore,
) : StudioGateway {

    override suspend fun upsertBook(book: BookDto): Result<BookDto> {
        delay(NETWORK_DELAY_MS)
        return Result.Success(store.upsertBook(book))
    }

    override suspend fun upsertCourse(course: CourseDto): Result<CourseDto> {
        delay(NETWORK_DELAY_MS)
        return Result.Success(store.upsertCourse(course))
    }

    override suspend fun upsertArticle(article: ArticleDto): Result<ArticleDto> {
        delay(NETWORK_DELAY_MS)
        return Result.Success(store.upsertArticle(article))
    }

    override suspend fun upsertContest(
        contest: ContestDto,
        questions: List<QuizQuestionDto>,
    ): Result<ContestDto> {
        delay(NETWORK_DELAY_MS)
        return Result.Success(store.upsertContest(contest, questions))
    }

    override suspend fun deleteBook(id: String): Result<Unit> {
        delay(NETWORK_DELAY_MS)
        return if (store.deleteBook(id)) Result.Success(Unit) else Result.Error(NoSuchElementException("کتاب یافت نشد"))
    }

    override suspend fun deleteCourse(id: String): Result<Unit> {
        delay(NETWORK_DELAY_MS)
        return if (store.deleteCourse(id)) Result.Success(Unit) else Result.Error(NoSuchElementException("دوره یافت نشد"))
    }

    override suspend fun deleteArticle(id: String): Result<Unit> {
        delay(NETWORK_DELAY_MS)
        return if (store.deleteArticle(id)) Result.Success(Unit) else Result.Error(NoSuchElementException("مطلب یافت نشد"))
    }

    override suspend fun deleteContest(id: String): Result<Unit> {
        delay(NETWORK_DELAY_MS)
        return if (store.deleteContest(id)) Result.Success(Unit) else Result.Error(NoSuchElementException("مسابقه یافت نشد"))
    }

    override suspend fun getContestReport(contestId: String): Result<ContestReportDto> {
        delay(NETWORK_DELAY_MS)
        val report = store.contestReport(contestId)
        return if (report != null) {
            Result.Success(report)
        } else {
            Result.Error(NoSuchElementException("گزارش مسابقه یافت نشد"))
        }
    }

    override suspend fun getOrgMessages(): Result<List<OrgMessageDto>> {
        delay(NETWORK_DELAY_MS)
        return Result.Success(store.orgMessages.value)
    }

    override suspend fun sendOrgMessage(
        title: String,
        body: String,
        recipient: String,
    ): Result<OrgMessageDto> {
        delay(NETWORK_DELAY_MS)
        return Result.Success(
            store.sendOrgMessage(
                fromName = "شما",
                fromRole = "ORGANIZATIONAL",
                title = title,
                body = body,
                recipient = recipient,
            ),
        )
    }

    override suspend fun markOrgMessageRead(id: String): Result<OrgMessageDto> {
        delay(NETWORK_DELAY_MS)
        val updated = store.markOrgMessageRead(id)
        return if (updated != null) {
            Result.Success(updated)
        } else {
            Result.Error(NoSuchElementException("پیام یافت نشد"))
        }
    }

    override suspend fun getReport(): Result<PlatformReportDto> {
        delay(NETWORK_DELAY_MS)
        return Result.Success(store.report())
    }

    override suspend fun getStaff(): Result<List<StaffMemberDto>> {
        delay(NETWORK_DELAY_MS)
        return Result.Success(store.staff.value)
    }

    override suspend fun updateStaffRole(userId: String, role: String): Result<StaffMemberDto> {
        delay(NETWORK_DELAY_MS)
        val updated = store.updateRole(userId, role)
        return if (updated != null) {
            Result.Success(updated)
        } else {
            Result.Error(NoSuchElementException("کاربر یافت نشد"))
        }
    }

    companion object {
        private const val NETWORK_DELAY_MS = 180L
    }
}
