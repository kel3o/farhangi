package ir.farhangi.core.data.repository

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.common.result.map
import ir.farhangi.core.data.mapper.toDomain
import ir.farhangi.core.model.Contest
import ir.farhangi.core.model.LeaderboardEntry
import ir.farhangi.core.model.LeaderboardPeriod
import ir.farhangi.core.model.PointsBreakdown
import ir.farhangi.core.model.QuizQuestion
import ir.farhangi.core.model.QuizSubmissionResult
import ir.farhangi.core.model.ScoreBoard
import ir.farhangi.core.model.Trophy
import ir.farhangi.core.network.gateway.EngagementGateway
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultEngagementRepository @Inject constructor(
    private val engagementGateway: EngagementGateway,
) : EngagementRepository {

    override suspend fun getContests(): Result<List<Contest>> =
        engagementGateway.getContests().map { list -> list.map { it.toDomain() } }

    override suspend fun getContest(id: String): Result<Contest> =
        engagementGateway.getContest(id).map { it.toDomain() }

    override suspend fun getQuestions(contestId: String): Result<List<QuizQuestion>> =
        engagementGateway.getQuestions(contestId).map { list -> list.map { it.toDomain() } }

    override suspend fun submitQuiz(
        contestId: String,
        answers: Map<String, Int>,
    ): Result<QuizSubmissionResult> {
        val questionsResult = getQuestions(contestId)
        val total = when (questionsResult) {
            is Result.Success -> questionsResult.data.size
            is Result.Error -> return questionsResult
            Result.Loading -> 0
        }
        return engagementGateway.submitQuiz(contestId, answers).map { percent ->
            val correct = (percent * total) / PERCENT_BASE
            QuizSubmissionResult(
                contestId = contestId,
                correctCount = correct,
                totalCount = total,
                percent = percent,
            )
        }
    }

    override suspend fun getLeaderboard(
        period: LeaderboardPeriod,
        board: ScoreBoard,
    ): Result<List<LeaderboardEntry>> =
        engagementGateway.getLeaderboard(period.name, board.name).map { list ->
            list.map { it.toDomain(CURRENT_USER_ID) }
        }

    override suspend fun getPoints(): Result<PointsBreakdown> =
        engagementGateway.getPoints().map { it.toDomain() }

    override suspend fun getTrophies(): Result<List<Trophy>> =
        engagementGateway.getTrophies().map { list -> list.map { it.toDomain() } }

    override suspend fun getSavedBookIds(): Result<Set<String>> = engagementGateway.getSavedBookIds()

    override suspend fun toggleSavedBook(bookId: String): Result<Set<String>> =
        engagementGateway.toggleSavedBook(bookId)

    override suspend fun addReadingMinutes(minutes: Int): Result<PointsBreakdown> =
        engagementGateway.addReadingMinutes(minutes).map { it.toDomain() }

    companion object {
        private const val PERCENT_BASE = 100
        private const val CURRENT_USER_ID = "demo-user"
    }
}
