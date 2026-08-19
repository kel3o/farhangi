package ir.farhangi.core.network.gateway

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.network.model.ArticleDto
import ir.farhangi.core.network.model.BookDto
import ir.farhangi.core.network.model.ContestDto
import ir.farhangi.core.network.model.CourseDto
import ir.farhangi.core.network.model.OrgMessageDto
import ir.farhangi.core.network.model.PlatformReportDto
import ir.farhangi.core.network.model.QuizQuestionDto
import ir.farhangi.core.network.model.StaffMemberDto

interface StudioGateway {
    suspend fun upsertBook(book: BookDto): Result<BookDto>
    suspend fun upsertCourse(course: CourseDto): Result<CourseDto>
    suspend fun upsertArticle(article: ArticleDto): Result<ArticleDto>
    suspend fun upsertContest(contest: ContestDto, questions: List<QuizQuestionDto>): Result<ContestDto>
    suspend fun getOrgMessages(): Result<List<OrgMessageDto>>
    suspend fun sendOrgMessage(title: String, body: String): Result<OrgMessageDto>
    suspend fun getReport(): Result<PlatformReportDto>
    suspend fun getStaff(): Result<List<StaffMemberDto>>
    suspend fun updateStaffRole(userId: String, role: String): Result<StaffMemberDto>
}
