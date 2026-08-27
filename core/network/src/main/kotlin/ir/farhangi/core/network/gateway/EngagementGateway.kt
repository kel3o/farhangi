package ir.farhangi.core.network.gateway

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.network.model.ContestDto
import ir.farhangi.core.network.model.LeaderboardEntryDto
import ir.farhangi.core.network.model.PointsDto
import ir.farhangi.core.network.model.QuizQuestionDto
import ir.farhangi.core.network.model.TrophyDto

interface EngagementGateway {
    suspend fun getContests(): Result<List<ContestDto>>
    suspend fun getContest(id: String): Result<ContestDto>
    suspend fun getQuestions(contestId: String): Result<List<QuizQuestionDto>>
    suspend fun submitQuiz(contestId: String, answers: Map<String, Int>): Result<Int>
    suspend fun getLeaderboard(period: String, board: String): Result<List<LeaderboardEntryDto>>
    suspend fun getPoints(): Result<PointsDto>
    suspend fun getTrophies(): Result<List<TrophyDto>>
    suspend fun getSavedBookIds(): Result<Set<String>>
    suspend fun toggleSavedBook(bookId: String): Result<Set<String>>
    suspend fun getSavedArticleIds(): Result<Set<String>>
    suspend fun toggleSavedArticle(articleId: String): Result<Set<String>>
    suspend fun addReadingMinutes(minutes: Int): Result<PointsDto>
}
