package ir.farhangi.core.data.mapper

import ir.farhangi.core.model.Contest
import ir.farhangi.core.model.ContestCategory
import ir.farhangi.core.model.ContestStatus
import ir.farhangi.core.model.LeaderboardEntry
import ir.farhangi.core.model.LeaderboardPeriod
import ir.farhangi.core.model.NamedCount
import ir.farhangi.core.model.OrgMessage
import ir.farhangi.core.model.PlatformReport
import ir.farhangi.core.model.PointsBreakdown
import ir.farhangi.core.model.QuizQuestion
import ir.farhangi.core.model.ScoreBoard
import ir.farhangi.core.model.StaffMember
import ir.farhangi.core.model.Trophy
import ir.farhangi.core.model.UserRole
import ir.farhangi.core.network.model.ContestDto
import ir.farhangi.core.network.model.LeaderboardEntryDto
import ir.farhangi.core.network.model.NamedCountDto
import ir.farhangi.core.network.model.OrgMessageDto
import ir.farhangi.core.network.model.PlatformReportDto
import ir.farhangi.core.network.model.PointsDto
import ir.farhangi.core.network.model.QuizQuestionDto
import ir.farhangi.core.network.model.StaffMemberDto
import ir.farhangi.core.network.model.TrophyDto
import kotlinx.datetime.Instant

fun ContestDto.toDomain(): Contest = Contest(
    id = id,
    title = title,
    summary = summary,
    category = runCatching { ContestCategory.valueOf(category) }.getOrDefault(ContestCategory.GENERAL_KNOWLEDGE),
    status = runCatching { ContestStatus.valueOf(status) }.getOrDefault(ContestStatus.LIVE),
    questionCount = questionCount,
    participantCount = participantCount,
    relatedBookId = relatedBookId,
    relatedCourseId = relatedCourseId,
    endsAt = Instant.parse(endsAt),
    userScorePercent = userScorePercent,
)

fun QuizQuestionDto.toDomain(): QuizQuestion = QuizQuestion(
    id = id,
    prompt = prompt,
    options = options,
)

fun LeaderboardEntryDto.toDomain(currentUserId: String): LeaderboardEntry = LeaderboardEntry(
    rank = rank,
    userId = userId,
    displayName = displayName,
    points = points,
    isCurrentUser = userId == currentUserId,
)

fun PointsDto.toDomain(): PointsBreakdown = PointsBreakdown(
    reading = reading,
    courses = courses,
    contests = contests,
    magazine = magazine,
)

fun TrophyDto.toDomain(): Trophy = Trophy(
    id = id,
    title = title,
    period = runCatching { LeaderboardPeriod.valueOf(period) }.getOrDefault(LeaderboardPeriod.WEEKLY),
    board = runCatching { ScoreBoard.valueOf(board) }.getOrDefault(ScoreBoard.OVERALL),
    weekOrMonthLabel = weekOrMonthLabel,
)

fun OrgMessageDto.toDomain(): OrgMessage = OrgMessage(
    id = id,
    fromName = fromName,
    fromRole = runCatching { UserRole.valueOf(fromRole) }.getOrDefault(UserRole.ORGANIZATIONAL),
    title = title,
    body = body,
    createdAt = Instant.parse(createdAt),
    isRead = isRead,
)

fun PlatformReportDto.toDomain(): PlatformReport = PlatformReport(
    sectionAudience = sectionAudience.map { it.toDomain() },
    topBooks = topBooks.map { it.toDomain() },
    topCourses = topCourses.map { it.toDomain() },
    topArticles = topArticles.map { it.toDomain() },
)

fun NamedCountDto.toDomain(): NamedCount = NamedCount(name = name, count = count)

fun StaffMemberDto.toDomain(): StaffMember = StaffMember(
    id = id,
    displayName = displayName,
    phone = phone,
    role = runCatching { UserRole.valueOf(role) }.getOrDefault(UserRole.USER),
)
