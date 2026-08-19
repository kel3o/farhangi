package ir.farhangi.core.network.demo

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.network.gateway.EngagementGateway
import ir.farhangi.core.network.model.ContestDto
import ir.farhangi.core.network.model.LeaderboardEntryDto
import ir.farhangi.core.network.model.PointsDto
import ir.farhangi.core.network.model.QuizQuestionDto
import ir.farhangi.core.network.model.TrophyDto
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DemoEngagementGateway @Inject constructor(
    private val store: DemoPlatformStore,
) : EngagementGateway {

    override suspend fun getContests(): Result<List<ContestDto>> {
        delay(NETWORK_DELAY_MS)
        return Result.Success(store.contests.value)
    }

    override suspend fun getContest(id: String): Result<ContestDto> {
        delay(NETWORK_DELAY_MS)
        return store.contests.value.find { it.id == id }?.let { Result.Success(it) }
            ?: Result.Error(NoSuchElementException("مسابقه یافت نشد"))
    }

    override suspend fun getQuestions(contestId: String): Result<List<QuizQuestionDto>> {
        delay(NETWORK_DELAY_MS)
        val questions = store.questionsByContest.value[contestId]
            ?: return Result.Error(NoSuchElementException("سؤال‌ها یافت نشد"))
        return Result.Success(questions)
    }

    override suspend fun submitQuiz(contestId: String, answers: Map<String, Int>): Result<Int> {
        delay(NETWORK_DELAY_MS)
        return Result.Success(store.submitQuiz(contestId, answers))
    }

    override suspend fun getLeaderboard(period: String, board: String): Result<List<LeaderboardEntryDto>> {
        delay(NETWORK_DELAY_MS)
        val rows = if (period.equals("MONTHLY", ignoreCase = true)) {
            store.monthlyLeaderboard(board)
        } else {
            store.weeklyLeaderboard(board)
        }
        return Result.Success(rows)
    }

    override suspend fun getPoints(): Result<PointsDto> {
        delay(NETWORK_DELAY_MS)
        return Result.Success(store.points.value)
    }

    override suspend fun getTrophies(): Result<List<TrophyDto>> {
        delay(NETWORK_DELAY_MS)
        return Result.Success(store.trophies.value)
    }

    override suspend fun getSavedBookIds(): Result<Set<String>> {
        delay(NETWORK_DELAY_MS)
        return Result.Success(store.savedBookIds.value)
    }

    override suspend fun toggleSavedBook(bookId: String): Result<Set<String>> {
        delay(NETWORK_DELAY_MS)
        return Result.Success(store.toggleSaved(bookId))
    }

    override suspend fun addReadingMinutes(minutes: Int): Result<PointsDto> {
        delay(NETWORK_DELAY_MS)
        return Result.Success(store.addReadingMinutes(minutes))
    }

    companion object {
        private const val NETWORK_DELAY_MS = 180L
    }
}
