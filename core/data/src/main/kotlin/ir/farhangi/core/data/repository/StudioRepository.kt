package ir.farhangi.core.data.repository

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.model.Article
import ir.farhangi.core.model.Book
import ir.farhangi.core.model.Contest
import ir.farhangi.core.model.Course
import ir.farhangi.core.model.OrgInboxRecipient
import ir.farhangi.core.model.OrgMessage
import ir.farhangi.core.model.PlatformReport
import ir.farhangi.core.model.QuizQuestion
import ir.farhangi.core.model.ContestReport
import ir.farhangi.core.model.StaffMember
import ir.farhangi.core.model.UserRole

interface StudioRepository {
    suspend fun upsertBook(book: Book): Result<Book>
    suspend fun upsertCourse(course: Course): Result<Course>
    suspend fun upsertArticle(article: Article): Result<Article>
    suspend fun upsertContest(contest: Contest, questions: List<QuizQuestion>): Result<Contest>
    suspend fun deleteBook(id: String): Result<Unit>
    suspend fun deleteCourse(id: String): Result<Unit>
    suspend fun deleteArticle(id: String): Result<Unit>
    suspend fun deleteContest(id: String): Result<Unit>
    suspend fun getContestReport(contestId: String): Result<ContestReport>
    suspend fun getOrgMessages(): Result<List<OrgMessage>>
    suspend fun getOrgMessage(id: String): Result<OrgMessage>
    suspend fun sendOrgMessage(
        title: String,
        body: String,
        recipient: OrgInboxRecipient,
    ): Result<OrgMessage>
    suspend fun markOrgMessageRead(id: String): Result<OrgMessage>
    suspend fun getReport(): Result<PlatformReport>
    suspend fun getStaff(): Result<List<StaffMember>>
    suspend fun updateStaffRole(userId: String, role: UserRole): Result<StaffMember>
}
