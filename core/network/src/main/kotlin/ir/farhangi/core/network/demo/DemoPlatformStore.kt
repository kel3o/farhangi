package ir.farhangi.core.network.demo

import ir.farhangi.core.network.model.AnnouncementDto
import ir.farhangi.core.network.model.ArticleDto
import ir.farhangi.core.network.model.BookDto
import ir.farhangi.core.network.model.ContestDto
import ir.farhangi.core.network.model.CourseDto
import ir.farhangi.core.network.model.LeaderboardEntryDto
import ir.farhangi.core.network.model.NamedCountDto
import ir.farhangi.core.network.model.OrgMessageDto
import ir.farhangi.core.network.model.PlatformReportDto
import ir.farhangi.core.network.model.PointsDto
import ir.farhangi.core.network.model.QuizQuestionDto
import ir.farhangi.core.network.model.StaffMemberDto
import ir.farhangi.core.network.model.TrophyDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DemoPlatformStore @Inject constructor() {

    val books = MutableStateFlow(seedBooks())
    val courses = MutableStateFlow(seedCourses())
    val articles = MutableStateFlow(seedArticles())
    val announcements = MutableStateFlow(seedAnnouncements())
    val contests = MutableStateFlow(seedContests())
    val questionsByContest = MutableStateFlow(seedQuestions())
    val savedBookIds = MutableStateFlow(setOf("book-1", "book-4"))
    val savedArticleIds = MutableStateFlow(setOf("article-1"))
    val points = MutableStateFlow(PointsDto(reading = 240, courses = 80, contests = 60, magazine = 35))
    val trophies = MutableStateFlow(
        listOf(
            TrophyDto("t1", "جام هفته مطالعه", "WEEKLY", "READING", "هفته جاری"),
            TrophyDto("t2", "جام ماه هم‌خوان", "MONTHLY", "OVERALL", "مرداد ۱۴۰۵"),
        ),
    )
    val orgMessages = MutableStateFlow(seedOrgMessages())
    val staff = MutableStateFlow(seedStaff())
    val quizScores = MutableStateFlow<Map<String, Int>>(emptyMap())

    fun upsertBook(book: BookDto): BookDto {
        val stored = if (book.id.isBlank()) book.copy(id = "book-${UUID.randomUUID()}") else book
        books.update { current ->
            val without = current.filterNot { it.id == stored.id }
            listOf(stored) + without
        }
        return stored
    }

    fun upsertCourse(course: CourseDto): CourseDto {
        val stored = if (course.id.isBlank()) course.copy(id = "course-${UUID.randomUUID()}") else course
        courses.update { current ->
            val without = current.filterNot { it.id == stored.id }
            listOf(stored) + without
        }
        return stored
    }

    fun upsertArticle(article: ArticleDto): ArticleDto {
        val stored = if (article.id.isBlank()) article.copy(id = "article-${UUID.randomUUID()}") else article
        articles.update { current ->
            val without = current.filterNot { it.id == stored.id }
            listOf(stored) + without
        }
        return stored
    }

    fun upsertContest(contest: ContestDto, questions: List<QuizQuestionDto>): ContestDto {
        val stored = if (contest.id.isBlank()) contest.copy(id = "contest-${UUID.randomUUID()}") else contest
        contests.update { current ->
            val without = current.filterNot { it.id == stored.id }
            listOf(stored) + without
        }
        questionsByContest.update { it + (stored.id to questions) }
        return stored
    }

    fun toggleSaved(bookId: String): Set<String> {
        savedBookIds.update { current ->
            if (bookId in current) current - bookId else current + bookId
        }
        return savedBookIds.value
    }

    fun toggleSavedArticle(articleId: String): Set<String> {
        savedArticleIds.update { current ->
            if (articleId in current) current - articleId else current + articleId
        }
        return savedArticleIds.value
    }

    fun addReadingMinutes(minutes: Int): PointsDto {
        points.update { it.copy(reading = it.reading + minutes) }
        return points.value
    }

    fun submitQuiz(contestId: String, answers: Map<String, Int>): Int {
        val questions = questionsByContest.value[contestId].orEmpty()
        val correct = questions.count { question -> answers[question.id] == question.correctIndex }
        val percent = if (questions.isEmpty()) 0 else (correct * PERCENT_BASE) / questions.size
        quizScores.update { it + (contestId to percent) }
        val pointsPerCorrect = contests.value.find { it.id == contestId }?.pointsPerCorrect
            ?: CONTEST_POINT_PER_CORRECT
        points.update { it.copy(contests = it.contests + correct * pointsPerCorrect) }
        contests.update { list ->
            list.map { contest ->
                if (contest.id == contestId) contest.copy(userScorePercent = percent) else contest
            }
        }
        return percent
    }

    fun sendOrgMessage(fromName: String, fromRole: String, title: String, body: String): OrgMessageDto {
        val message = OrgMessageDto(
            id = "msg-${UUID.randomUUID()}",
            fromName = fromName,
            fromRole = fromRole,
            title = title,
            body = body,
            createdAt = "2026-08-18T12:00:00Z",
            isRead = false,
        )
        orgMessages.update { listOf(message) + it }
        return message
    }

    fun updateRole(userId: String, role: String): StaffMemberDto? {
        var updated: StaffMemberDto? = null
        staff.update { list ->
            list.map { member ->
                if (member.id == userId) {
                    member.copy(role = role).also { updated = it }
                } else {
                    member
                }
            }
        }
        return updated
    }

    fun report(): PlatformReportDto = PlatformReportDto(
        sectionAudience = listOf(
            NamedCountDto("کتاب", 1840),
            NamedCountDto("آموزش", 920),
            NamedCountDto("مسابقه", 640),
            NamedCountDto("مجله", 1510),
            NamedCountDto("هم‌خوان", 430),
        ),
        topBooks = books.value.take(TOP_ITEMS).map { NamedCountDto(it.title, 120 + it.title.length) },
        topCourses = courses.value.take(TOP_ITEMS).map { NamedCountDto(it.title, 80 + it.title.length) },
        topArticles = articles.value.take(TOP_ITEMS).map { NamedCountDto(it.title, 90 + it.title.length) },
    )

    fun weeklyLeaderboard(board: String): List<LeaderboardEntryDto> = leaderboardFor(board, weekly = true)

    fun monthlyLeaderboard(board: String): List<LeaderboardEntryDto> = leaderboardFor(board, weekly = false)

    private fun leaderboardFor(board: String, weekly: Boolean): List<LeaderboardEntryDto> {
        val userPoints = when (board) {
            "READING" -> points.value.reading
            "COURSES" -> points.value.courses
            "CONTESTS" -> points.value.contests
            "MAGAZINE" -> points.value.magazine
            else -> points.value.reading + points.value.courses + points.value.contests + points.value.magazine
        }
        val names = if (weekly) WEEKLY_NAMES else MONTHLY_NAMES
        val base = if (weekly) WEEKLY_BASE else MONTHLY_BASE
        val others = names.mapIndexed { index, name ->
            LeaderboardEntryDto(
                rank = 0,
                userId = "u$index",
                displayName = name,
                points = base - (index * POINT_STEP),
            )
        }
        val merged = (others + LeaderboardEntryDto(0, CURRENT_USER_ID, "شما", userPoints))
            .sortedByDescending { it.points }
            .take(LEADERBOARD_LIMIT)
            .mapIndexed { index, entry -> entry.copy(rank = index + 1) }
        return merged
    }

    companion object {
        const val CURRENT_USER_ID = "demo-user"
        private const val PERCENT_BASE = 100
        private const val CONTEST_POINT_PER_CORRECT = 10
        private const val TOP_ITEMS = 3
        private const val LEADERBOARD_LIMIT = 10
        private const val POINT_STEP = 18
        private const val WEEKLY_BASE = 420
        private const val MONTHLY_BASE = 980
        private val WEEKLY_NAMES = listOf(
            "زهرا محمدی", "علی رضایی", "مریم حسینی", "حسین کاظمی",
            "فاطمه نوری", "رضا اکبری", "سارا کریمی", "مهدی جعفری", "نرگس امینی",
        )
        private val MONTHLY_NAMES = listOf(
            "فاطمه نوری", "علی رضایی", "زهرا محمدی", "سارا کریمی",
            "حسین کاظمی", "مریم حسینی", "مهدی جعفری", "رضا اکبری", "نرگس امینی",
        )
    }
}

private fun seedArticles(): List<ArticleDto> = listOf(
    ArticleDto("article-1", "چایخانه، نه موزه", "TEXT", "CULTURE", "سنت وقتی زنده است که محل رفت‌وآمد باشد.", "سنت را اگر فقط پشت شیشه بگذاریم، می‌میرد. چایخانه یعنی حرف زدن با صدای معمولی درباره چیزهای مهم. در چایخانه می‌شود از کتاب تازه گفت، از خاطره مادربزرگ، و از برنامه‌ای که فردا برای محله داریم. زنده بودن سنت یعنی رفت‌وآمد، نه ویترین. اگر جای امنی برای حرف زدن نباشد، فرهنگ به عکس و قاب تقلیل پیدا می‌کند. پس چایخانه را نه به‌عنوان نوستالژی، که به‌عنوان فضای عمومی کوچک ببینیم.", null, "cover_article_1", "2026-08-17T08:00:00Z"),
    ArticleDto("article-2", "یک شب بدون گوشی", "TEXT", "FAMILY", "آزمایش ساده‌ای که بیشتر خانواده‌ها از آن می‌ترسند.", "گوشی‌ها را در کشو بگذارید. بازی رومیزی یا کتاب بلندخوانی. اولین بیست دقیقه سخت است؛ بعدش خانه صدا پیدا می‌کند. بسیاری از والدین می‌گویند «وقت نداریم»، اما همان شب می‌فهمند زمان آزاد بوده و توجه پراکنده بوده است. یک شب کافی است تا ببینید گفت‌وگو چقدر تشنه‌ی سکوت گوشی است. فردا لازم نیست کامل قطع کنید؛ همان یک ساعت را نگه دارید.", null, "cover_article_2", "2026-08-16T18:00:00Z"),
    ArticleDto("article-3", "هفته کتاب در کتابخانه‌های محلی", "NEWS", "NEWS", "برنامه امانت ویژه و نشست قصه‌خوانی برای نوجوانان.", "کتابخانه‌های محله این هفته امانت دوم را رایگان کرده‌اند. اگر عضو نیستید، همان روز ثبت‌نام کنید. نشست قصه‌خوانی عصرها برگزار می‌شود و مناسب خانواده‌هایی است که دنبال فعالیت کم‌هزینه هستند. خبر کوتاه است، اما اثرش در قفسه‌ی خانه دیده می‌شود: کتاب تازه، گفت‌وگوی تازه.", null, "cover_article_3", "2026-08-18T07:30:00Z"),
    ArticleDto("article-4", "کتاب ارزان‌تر از قهوه", "TEXT", "ECONOMY", "حساب‌وکتاب کوچک برای عادت مطالعه.", "اگر هفته‌ای دو قهوه را به یک کتاب دست‌دوم تبدیل کنید، تا پایان سال یک قفسه دارید. اقتصاد فرهنگی از همین تصمیم‌های کوچک است. لازم نیست بودجه بزرگ داشته باشید؛ لازم است اولویت را جابه‌جا کنید. کتابی که ورق می‌خورد، برخلاف نوشیدنی، دوباره به کار می‌آید و گاهی دست نسل بعد می‌رسد.", null, "cover_article_4", "2026-08-15T11:00:00Z"),
    ArticleDto("article-5", "ادب مخالفت", "TEXT", "POLITICS", "گفت‌وگوی عمومی بدون خرد کردن طرف مقابل.", "سیاست فرهنگی یعنی بتوانیم مخالف باشیم و هنوز به یک صف نانوایی احترام بگذاریم. توهین، میان‌بر گرانی است. وقتی زبان تند می‌شود، گوش‌ها بسته می‌شوند و مسئله حل نمی‌شود. ادب مخالفت تمرین صبر است: حرف را بشنو، دلیل بیاور، و شخصیت طرف را له نکن. این مهارت از خانه و مدرسه شروع می‌شود، نه فقط از تریبون.", null, "cover_article_5", "2026-08-14T09:00:00Z"),
    ArticleDto("article-6", "ده دقیقه قبل از خواب", "TEXT", "READING", "کوچک‌ترین سهمی که کتاب را زنده نگه می‌دارد.", "موبایل را یک اتاق آن‌طرف‌تر شارژ کنید. ده صفحه، نه یک جلد. فردا همان خط را پیدا می‌کنید. عادت‌های بزرگ از بازه‌های کوتاه ساخته می‌شوند. اگر منتظر «وقت آزاد طولانی» بمانید، کتاب روی میز خاک می‌خورد. ده دقیقه منظم، از دو ساعت اتفاقی جلوتر است.", null, "cover_article_6", "2026-08-13T21:00:00Z"),
    ArticleDto("article-7", "خط روی دیوار آشپزخانه", "TEXT", "ART", "خوشنویسی لازم نیست قاب موزه باشد.", "یک سطر نستعلیق بالای ظرف‌ها، هر روز چشم را تمرین می‌دهد. هنر وقتی در مسیر چای است، فراموش نمی‌شود. لازم نیست کارگاه حرفه‌ای داشته باشید؛ یک مشق کوتاه و یک قاب ساده کافی است تا خانه رنگ فرهنگی بگیرد. هنر کاربردی، هنر زنده‌است.", null, "cover_article_7", "2026-08-12T16:00:00Z"),
    ArticleDto("article-8", "کتابخانه‌های نیشابور چه شدند؟", "TEXT", "HISTORY", "یادآوری اینکه دانش هم می‌تواند آتش بگیرد.", "سده‌ها پیش نیشابور ایستگاه کاغذ و درس بود. تاریخ را بخوانیم تا فقط به ویرانه فکر نکنیم؛ به همت بازسازی هم فکر کنیم. کتابخانه اگر نباشد، حافظه جمعی ضعیف می‌شود. امروز هم کتابخانه‌های محلی همان نقش را دارند، فقط با ساختمانی ساده‌تر و مردمی صبورتر.", null, "cover_article_8", "2026-08-11T10:00:00Z"),
    ArticleDto("article-9", "جلدهای کهنه مادربزرگ", "TEXT", "NARRATIVE", "بوی کاغذ و دارچین.", "مادربزرگ اسم شخصیت‌ها را عوض می‌کرد تا به نوه برسند. روایت خانوادگی اگر نوشته نشود، با یک نسل قطع می‌شود. جلد کهنه فقط شیء نیست؛ پرونده‌ای از صداها و شب‌های بلندخوانی است. اگر فرصت دارید، یکی از همان قصه‌ها را روی کاغذ یا در گوشی ثبت کنید؛ آینده از همین یادداشت‌ها ساخته می‌شود.", null, "cover_article_9", "2026-08-10T19:00:00Z"),
)

private fun seedAnnouncements(): List<AnnouncementDto> = listOf(
    AnnouncementDto("an-1", "مسابقه گلستان آغاز شد", "تا پایان هفته فرصت دارید در مسابقه کتاب گلستان شرکت کنید.", "2026-08-18T06:00:00Z"),
)

private fun seedContests(): List<ContestDto> = listOf(
    ContestDto(
        "contest-1",
        "مسابقه گلستان",
        "چهار سؤال چهارگزینه‌ای از حکایت‌های آغاز گلستان سعدی. هدف این مسابقه فقط حفظ کردن نیست؛ می‌خواهیم ببینید پیام اخلاقی حکایت‌ها را چقدر در زندگی روزمره تشخیص می‌دهید. زمان محدود است، پس آرام و دقیق بخوانید. پس از ارسال پاسخ، درصد شما ذخیره می‌شود و می‌توانید بعداً در جدول هم‌خوان پیشرفت خود را ببینید. اگر هنوز کتاب را ورق نزده‌اید، یک صفحه از گلستان را مرور کنید و برگردید.",
        "BOOK",
        "LIVE",
        4,
        218,
        "book-1",
        null,
        "2026-09-05T20:00:00Z",
        null,
        180,
        10,
    ),
    ContestDto(
        "contest-2",
        "دانستنی‌های سبک زندگی",
        "سؤال‌هایی از عادت خانه، مدیریت زمان خانواده و مطالعه مشترک. این آزمون برای والدینی طراحی شده که می‌خواهند بدون شعار، قدم‌های کوچک و واقعی بردارند. هر سؤال یک موقعیت آشنا را مطرح می‌کند. پاسخ درست معمولاً ساده‌ترین و پایدارترین انتخاب است، نه سخت‌ترین. زمان پاسخ‌گویی محدود است و نتیجه در پرونده فرهنگی شما ثبت می‌شود.",
        "LIFESTYLE",
        "LIVE",
        4,
        96,
        null,
        "course-2",
        "2026-08-30T20:00:00Z",
        null,
        240,
        15,
    ),
    ContestDto(
        "contest-3",
        "اطلاعات عمومی فرهنگی",
        "ترکیبی از تاریخ، هنر، کتاب و نمادهای فرهنگی ایران و جهان. مناسب کسانی است که مجله و بخش کتاب را دنبال می‌کنند. سؤال‌ها کوتاه‌اند اما گزینه‌های انحرافی دارند؛ عجله نکنید. این مسابقه برای سنجش دانش سطحی نیست؛ برای زنده نگه داشتن کنجکاوی فرهنگی است. پس از پایان، می‌توانید منابع پیشنهادی را در مجله بخوانید.",
        "GENERAL_KNOWLEDGE",
        "LIVE",
        4,
        154,
        null,
        null,
        "2026-09-10T20:00:00Z",
        null,
        300,
        10,
    ),
    ContestDto(
        "contest-4",
        "آزمون دوره خوشنویسی",
        "ویژه کسانی که مسیر تخصصی خوشنویسی کاربردی را دیده‌اند یا مشق اولیه را تجربه کرده‌اند. سؤال‌ها درباره ابزار، نشستن، زاویه قلم و اشتباه‌های رایج است. اگر هنوز دوره را تمام نکرده‌اید، می‌توانید برای آشنایی شرکت کنید؛ اما بهترین نتیجه بعد از تمرین کوتاه روزانه به‌دست می‌آید. زمان آزمون فشرده است تا تمرکز حفظ شود.",
        "PRACTICAL_COURSE",
        "LIVE",
        4,
        41,
        null,
        "course-9",
        "2026-08-28T20:00:00Z",
        null,
        210,
        20,
    ),
    ContestDto(
        "contest-5",
        "مسابقه کیمیاگر",
        "مسابقه کتاب کیمیاگر به پایان رسیده و نتایج اعلام شده است. چهار سؤال از پیام اصلی داستان، مسیر چوپان و مفهوم افسانه شخصی مطرح شده بود. اگر شرکت کرده‌اید، درصد نهایی‌تان را در همین صفحه می‌بینید. برای دور بعدی مسابقات کتاب، اعلان‌های مجله و خانه را دنبال کنید. مرور دوباره چند صفحه از کتاب هنوز هم ارزش دارد.",
        "BOOK",
        "FINISHED",
        4,
        310,
        "book-2",
        null,
        "2026-08-10T20:00:00Z",
        75,
        270,
        10,
    ),
)

private fun seedQuestions(): Map<String, List<QuizQuestionDto>> = mapOf(
    "contest-1" to listOf(
        QuizQuestionDto("q1", "سعدی گلستان را بیشتر با چه نثری نوشته است؟", listOf("نثر مسجع", "فقط شعر آزاد", "نمایشنامه", "گزارش خبری"), 0),
        QuizQuestionDto("q2", "موضوع اصلی بسیاری از حکایت‌های گلستان چیست؟", listOf("دستور آشپزی", "اخلاق و تربیت", "جغرافیا", "نجوم"), 1),
        QuizQuestionDto("q3", "«منت خدای را عز و جل» آغاز کدام اثر است؟", listOf("بوستان", "شاهنامه", "گلستان", "دیوان حافظ"), 2),
        QuizQuestionDto("q4", "خواندن گلستان امروز چه کمکی می‌کند؟", listOf("فقط حفظ لغت دشوار", "تمرین حکمت کوتاه در زندگی", "جایگزین اخبار", "هیچ"), 1),
    ),
    "contest-2" to listOf(
        QuizQuestionDto("q1", "بهترین شروع عادت مطالعه در خانه کدام است؟", listOf("اجبار شبانه دو ساعت", "زمان کوتاه و ثابت", "فقط در تعطیلات", "فقط کتاب درسی"), 1),
        QuizQuestionDto("q2", "گوشی در زمان گفت‌وگوی خانواده بهتر است؟", listOf("روی میز رو به همه", "در کشو یا اتاق دیگر", "با صدای بلند", "همیشه در دست کودک"), 1),
        QuizQuestionDto("q3", "تقسیم کار خانه وقتی پایدار می‌ماند که؟", listOf("فقط یک نفر یادش باشد", "روی کاغذ یا تقویم دیده شود", "هر روز عوض شود", "پنهان بماند"), 1),
        QuizQuestionDto("q4", "بعد از خواندن کتاب با نوجوان چه کنیم؟", listOf("امتحان سخت", "یک سؤال گفت‌وگویی", "مقایسه با همسایه", "نمره از بیست"), 1),
    ),
    "contest-3" to listOf(
        QuizQuestionDto("q1", "نستعلیق بیشتر با کدام هنر شناخته می‌شود؟", listOf("خوشنویسی", "کشتی", "نجوم", "بازرگانی"), 0),
        QuizQuestionDto("q2", "فردوسی سراینده کدام اثر است؟", listOf("گلستان", "شاهنامه", "مثنوی", "بوستان"), 1),
        QuizQuestionDto("q3", "کتابخانه عمومی بیشتر چه نقشی دارد؟", listOf("انبار راکد", "محل امانت و نشست", "فقط بایگانی اداری", "فروشگاه"), 1),
        QuizQuestionDto("q4", "روایت خانوادگی چرا مهم است؟", listOf("جای درس ریاضی", "حافظه نسل‌ها را وصل می‌کند", "فقط برای موزه", "بی‌فایده است"), 1),
    ),
    "contest-4" to listOf(
        QuizQuestionDto("q1", "برای شروع خوشنویسی عجله یعنی؟", listOf("پیشرفت سریع", "احتمال شکستن حرف و دست خسته", "ضروری است", "جایگزین مشق"), 1),
        QuizQuestionDto("q2", "مشق روزانه کوتاه بهتر است یا کلاس بدون تمرین؟", listOf("کلاس بدون تمرین", "مشق کوتاه منظم", "هیچ‌کدام", "فقط نگاه کردن"), 1),
        QuizQuestionDto("q3", "قلم‌نی ابزار کدام هنر است؟", listOf("عکاسی", "خوشنویسی", "خیاطی", "نجاری"), 1),
        QuizQuestionDto("q4", "نقطه کج در نستعلیق چه می‌کند؟", listOf("زیباتر می‌شود", "سطر را ناهماهنگ نشان می‌دهد", "مهم نیست", "رنگ را عوض می‌کند"), 1),
    ),
    "contest-5" to listOf(
        QuizQuestionDto("q1", "شخصیت اصلی کیمیاگر در آغاز داستان چیست؟", listOf("بازرگان", "چوپان", "پادشاه", "ملوان"), 1),
        QuizQuestionDto("q2", "رؤیای او او را به کدام سو می‌کشاند؟", listOf("قطب شمال", "مصر", "چین", "هند"), 1),
        QuizQuestionDto("q3", "پیام تکراری کتاب چیست؟", listOf("رها کردن رؤیا", "دنبال کردن افسانه شخصی", "جمع کردن طلا در خانه", "جنگ"), 1),
        QuizQuestionDto("q4", "گنج در پایان نزدیک کجاست؟", listOf("جایی غیرمنتظره نسبت به شروع سفر", "روی ماه", "در بانک", "هیچ"), 0),
    ),
)

private fun seedOrgMessages(): List<OrgMessageDto> = listOf(
    OrgMessageDto("msg-1", "مدیرکل", "SUPER_ADMIN", "اولویت محتوا در شهریور", "روی مسابقه گلستان و دوره‌های خانواده تمرکز کنید. گزارش هفتگی تا چهارشنبه.", "2026-08-17T09:00:00Z", false),
    OrgMessageDto("msg-2", "هماهنگ‌کننده استان", "ORGANIZATIONAL", "درخواست نشست کتابخانه‌ای", "برای هفته کتاب، دو نشست قصه‌خوانی در کتابخانه مرکزی پیشنهاد می‌شود.", "2026-08-16T14:20:00Z", true),
)

private fun seedStaff(): List<StaffMemberDto> = listOf(
    StaffMemberDto("demo-user", "شما", "09000000000", "USER"),
    StaffMemberDto("u-editor", "نگار مرادی", "09111111111", "EDITOR"),
    StaffMemberDto("u-org", "سعید افشار", "09222222222", "ORGANIZATIONAL"),
    StaffMemberDto("u-admin", "مدیرکل فرهنگی", "09333333333", "SUPER_ADMIN"),
    StaffMemberDto("u-user", "مینا صالحی", "09120000000", "USER"),
)
