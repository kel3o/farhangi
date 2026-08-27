package ir.farhangi.core.data.repository

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.model.Contest
import ir.farhangi.core.model.LeaderboardEntry
import ir.farhangi.core.model.LeaderboardPeriod
import ir.farhangi.core.model.PointsBreakdown
import ir.farhangi.core.model.QuizQuestion
import ir.farhangi.core.model.QuizSubmissionResult
import ir.farhangi.core.model.ScoreBoard
import ir.farhangi.core.model.Trophy

interface EngagementRepository {
    suspend fun getContests(): Result<List<Contest>>
    suspend fun getContest(id: String): Result<Contest>
    suspend fun getQuestions(contestId: String): Result<List<QuizQuestion>>
    suspend fun submitQuiz(contestId: String, answers: Map<String, Int>): Result<QuizSubmissionResult>
    suspend fun getLeaderboard(period: LeaderboardPeriod, board: ScoreBoard): Result<List<LeaderboardEntry>>
    suspend fun getPoints(): Result<PointsBreakdown>
    suspend fun getTrophies(): Result<List<Trophy>>
    suspend fun getSavedBookIds(): Result<Set<String>>
    suspend fun toggleSavedBook(bookId: String): Result<Set<String>>
    suspend fun getSavedArticleIds(): Result<Set<String>>
    suspend fun toggleSavedArticle(articleId: String): Result<Set<String>>
    suspend fun addReadingMinutes(minutes: Int): Result<PointsBreakdown>
}
