package ir.farhangi.core.network.demo

import ir.farhangi.core.network.model.AnnouncementDto
import ir.farhangi.core.network.model.ArticleDto
import ir.farhangi.core.network.model.BookDto
import ir.farhangi.core.network.model.ContestDto
import ir.farhangi.core.network.model.CourseDto
import ir.farhangi.core.network.model.CourseSectionDto
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

private fun seedBooks(): List<BookDto> = listOf(
    BookDto(
        id = "book-1",
        title = "گلستان سعدی",
        author = "شیخ مصلح‌الدین سعدی شیرازی",
        publisher = "انتشارات خوارزمی",
        coverUrl = "cover_golestan",
        categories = listOf("ادبیات"),
        totalPages = 4,
        rating = 4.8,
        description = "حکایت‌هایی کوتاه که هنوز بعد از هفت قرن، به کار زندگی روزمره می‌آیند.",
        pages = listOf(
            "منت خدای را عز و جل که طاعتش موجب قربت است و به شکر اندرش مزید نعمت.",
            "هر نفسی که فرو می‌رود ممدّ حیات است و چون برمی‌آید مفرح ذات. پس در هر نفسی دو نعمت موجود است و بر هر نعمتی شکری واجب.",
            "باران رحمت بی‌حسابش همه را رسیده و خوان نعمت بی‌دریغش همه جا کشیده.",
            "فراش باد صبا را گفته تا فرش زمردین بگسترد و دایه ابر بهاری را فرموده تا بنات نبات در مهد زمین بپرورد.",
        ),
    ),
    BookDto(
        id = "book-2",
        title = "کیمیاگر",
        author = "پائولو کوئیلو",
        publisher = "نشر کاروان",
        coverUrl = "cover_kimiagar",
        categories = listOf("داستان و رمان"),
        totalPages = 3,
        rating = 4.5,
        description = "چوپانی که به‌جای ماندن در همان تپه، راهی شن‌های مصر می‌شود.",
        pages = listOf(
            "وقتی چیزی را می‌خواهی، تمام کائنات با تو هم‌داستان می‌شوند تا آن را به دست آوری.",
            "ترس از رنج، از خود رنج بدتر است. هیچ دلی هرگز به‌خاطر دنبال کردن رؤیا آسیب ندیده است.",
            "گنج همیشه همان‌جا نیست که انتظارش را داری؛ گاهی باید برگشت تا فهمید از اول چه داشتی.",
        ),
    ),
    BookDto(
        id = "book-3",
        title = "تاریخ تمدن ایران",
        author = "حسن پیرنیا و عباس اقبال آشتیانی",
        publisher = "انتشارات دنیای کتاب",
        coverUrl = "cover_tarikh_tamadon",
        categories = listOf("تاریخ"),
        totalPages = 3,
        rating = 4.6,
        description = "از تخت‌جمشید تا کتابخانه‌های نیشابور؛ تمدن یعنی حافظه جمعی.",
        pages = listOf(
            "تمدن ایران فقط سنگ و ستون نیست؛ خط، آب‌رسانی و مهمانی هم تمدن است.",
            "کتابخانه‌های خراسان در سده‌های میانه، ایستگاه دانش بودند نه انبار خاک‌گرفته.",
            "اگر تاریخ را فقط جنگ بنویسیم، نیمی از داستان را دور ریخته‌ایم.",
        ),
    ),
    BookDto(
        id = "book-4",
        title = "آداب معاشرت",
        author = "آزاده بهرامچی",
        publisher = "نشر قطره",
        coverUrl = "cover_adab_moasherat",
        categories = listOf("روان‌شناسی و موفقیت"),
        totalPages = 3,
        rating = 4.4,
        description = "راهنمای جامعه‌پذیری برای نوجوانان، جوانان و مهاجرین.",
        pages = listOf(
            "خانه وقتی آرام است که کسی وسط حرف دیگری نپرد.",
            "مهمانی خوب با سفره شروع نمی‌شود؛ با خوش‌آمدگویی تمام می‌شود.",
            "گوش دادن، ارزان‌ترین هدیه‌ای است که در خانه کمیاب شده.",
        ),
    ),
    BookDto(
        id = "book-5",
        title = "خوش‌نویسی",
        author = "استاد محمود نوری",
        publisher = "انتشارات یساولی",
        coverUrl = "cover_khoshnevisi",
        categories = listOf("آموزشی"),
        totalPages = 3,
        rating = 4.7,
        description = "از قطعهٔ مشق تا دیوار آشپزخانه؛ خط را می‌شود زندگی کرد.",
        pages = listOf(
            "قلم‌نی را اگر عجله کنی، حرف می‌شکند. آدم هم همین است.",
            "نستعلیق صبر می‌خواهد؛ یک نقطه کج، کل سطر را لو می‌دهد.",
            "مشق روزانه ده دقیقه، از کلاس طولانی بدون تمرین جلوتر است.",
        ),
    ),
    BookDto(
        id = "book-6",
        title = "اقتصاد به زبان ساده",
        author = "لس لیوینگستون",
        publisher = "نشر نی",
        coverUrl = "cover_eghtesad",
        categories = listOf("آموزشی"),
        totalPages = 3,
        rating = 4.2,
        description = "تورم و پس‌انداز بدون اصطلاح ترسناک.",
        pages = listOf(
            "پول وقتی کار می‌کند که برایش برنامه داشته باشی، نه فقط کیف.",
            "خرید هیجانی، مالیات پنهان تصمیم‌های عجول است.",
            "کتاب ارزان‌تر از خیلی تفریح‌هاست؛ اگر وقتش را جدی بگیری.",
        ),
    ),
    BookDto(
        id = "book-7",
        title = "قصه‌های شیرین مادربزرگ",
        author = "معصومه طاهری",
        publisher = "کانون پرورش فکری",
        coverUrl = "cover_ghese_madarbozorg",
        categories = listOf("کودک و نوجوان"),
        totalPages = 3,
        rating = 4.9,
        description = "ویژه کودکان و نوجوانان؛ قصه‌هایی که نباید با نسل بعدی قطع شوند.",
        pages = listOf(
            "مادربزرگ می‌گفت کتاب اگر زمین بخورد، باید ببوسی و سر جایش بگذاری.",
            "شب‌های برق‌رفته، رادیو و قصه جای صفحه را می‌گرفت.",
            "روایت خانوادگی، شناسنامه نامرئی ماست.",
        ),
    ),
    BookDto(
        id = "book-8",
        title = "مهارت گفتوگو",
        author = "سیمون گرانت",
        publisher = "نشر دانژه",
        coverUrl = "cover_maharat_goftogo",
        categories = listOf("روان‌شناسی و موفقیت"),
        totalPages = 3,
        rating = 4.3,
        description = "بیست راهکار کلیدی برای کامیابی زندگی زناشویی و گفت‌وگوی محترمانه.",
        pages = listOf(
            "مخالفت اگر با توهین شروع شود، به حقیقت نمی‌رسد.",
            "شنیدن دقیق، نیمی از سیاست فرهنگی است.",
            "ادب گفت‌وگو را نمی‌شود فقط در شعار نوشت؛ باید تمرین شود.",
        ),
    ),
)

private fun seedCourses(): List<CourseDto> = listOf(
    CourseDto(
        id = "course-1",
        title = "آشنایی با خوشنویسی",
        type = "PRACTICAL",
        instructor = "استاد محمود نوری",
        coverUrl = "cover_khoshnevisi",
        description = "یک نشست کامل برای شروع نستعلیق؛ ابزار، نشستن و اولین سطر.",
        category = "هنر",
        isFree = true,
        sections = listOf(
            CourseSectionDto(
                id = "c1s1",
                title = "خوشنویسی در یک نگاه",
                order = 1,
                durationMinutes = 18,
                contentType = "ARTICLE",
                body = "برای شروع به یک قلم‌نی، دوات و کاغذ گلاسه نیاز دارید. کمر صاف، آرنج آزاد، و عجله ممنوع. امروز فقط حرف «ا» را مشق کنید تا دست آرام شود.",
            ),
        ),
        progress = 0f,
    ),
    CourseDto(
        id = "course-2",
        title = "مدیریت زمان خانواده",
        type = "PRACTICAL",
        instructor = "زهرا احمدی",
        coverUrl = "cover_adab_moasherat",
        description = "یک راهنمای فشرده برای تقسیم کار خانه و زمان مطالعه فرزندان.",
        category = "خانواده",
        isFree = true,
        sections = listOf(
            CourseSectionDto(
                id = "c2s1",
                title = "تقویم دیواری، نه اپ شلوغ",
                order = 1,
                durationMinutes = 12,
                contentType = "ARTICLE",
                body = "سه ستون بکشید: باید، بهتر است، می‌تواند صبر کند. هر شب ده دقیقه با خانواده مرور کنید. گوشی را در همان ده دقیقه کنار بگذارید.",
            ),
        ),
        progress = 0f,
    ),
    CourseDto(
        id = "course-3",
        title = "سواد رسانه‌ای روزانه",
        type = "PRACTICAL",
        instructor = "دکتر علی رضایی",
        coverUrl = "cover_maharat_goftogo",
        description = "چطور خبر را از شایعه جدا کنیم؛ یک درس کامل.",
        category = "فرهنگ عمومی",
        isFree = true,
        sections = listOf(
            CourseSectionDto(
                id = "c3s1",
                title = "منبع، تاریخ، انگیزه",
                order = 1,
                durationMinutes = 15,
                contentType = "ARTICLE",
                body = "قبل از بازنشر سه سؤال بپرسید: چه کسی گفته؟ کی گفته؟ چه سودی از باورش می‌برد؟ اگر جواب ندارید، سکوت محترمانه‌تر از بازنشر است.",
            ),
        ),
        progress = 0f,
    ),
    CourseDto(
        id = "course-4",
        title = "مسیر ادبیات معاصر",
        type = "PROFESSIONAL",
        instructor = "دکتر لیلا موسوی",
        coverUrl = "cover_golestan",
        description = "سه جلسه برای فهم شعر و نثر معاصر فارسی؛ ویدیو و متن.",
        category = "ادبیات",
        isFree = true,
        sections = listOf(
            CourseSectionDto("c4s1", "چرا معاصر؟", 1, 22, contentType = "ARTICLE", body = "ادبیات معاصر از مشروطه به بعد با مسئله مردم حرف می‌زند؛ زبان ساده‌تر شد اما فکر پیچیده‌تر."),
            CourseSectionDto(
                id = "c4s2",
                title = "شعر نیمایی در تصویر",
                order = 2,
                durationMinutes = 28,
                contentType = "VIDEO",
                aparatUrl = "https://www.aparat.com",
                body = "وزن نیمایی را با چند مثال صوتی بشنوید و بعد یک بند از نیما را بلند بخوانید.",
            ),
            CourseSectionDto("c4s3", "نثر داستانی", 3, 30, contentType = "ARTICLE", body = "داستان کوتاه معاصر روی جزئیات روزمره بند است. یک صحنه از خانه خودتان را در ده خط بنویسید."),
        ),
        progress = 0.0f,
    ),
    CourseDto(
        id = "course-5",
        title = "تربیت مطالعه در نوجوان",
        type = "PROFESSIONAL",
        instructor = "مریم حسینی",
        coverUrl = "cover_ghese_madarbozorg",
        description = "مسیر چهارجلسه‌ای برای والدین؛ از انتخاب کتاب تا گفت‌وگوی بعد از خواندن.",
        category = "خانواده",
        isFree = true,
        sections = listOf(
            CourseSectionDto("c5s1", "کتاب را تحمیل نکنید", 1, 20, contentType = "ARTICLE", body = "نوجوان اگر حق انتخاب داشته باشد، مقاومت کمتر می‌شود. سه گزینه بگذارید، یکی را او بردارد."),
            CourseSectionDto("c5s2", "زمان ثابت، نه ساعت طولانی", 2, 18, contentType = "ARTICLE", body = "بیست دقیقه ثابت بهتر از دو ساعت اتفاقی است. بعد از مطالعه، یک سؤال بپرسید نه امتحان."),
            CourseSectionDto(
                id = "c5s3",
                title = "گفت‌وگوی خانوادگی",
                order = 3,
                durationMinutes = 24,
                contentType = "VIDEO",
                aparatUrl = "https://www.aparat.com",
                body = "نمونه‌ای از گفت‌وگوی کوتاه بعد از داستان؛ بدون نمره و مقایسه با دیگران.",
            ),
            CourseSectionDto("c5s4", "کتابخانه خانگی کوچک", 4, 16, contentType = "ARTICLE", body = "یک قفسه دم دست، نه انبار. جلد روبه‌رو باشد تا دعوت کند."),
        ),
        progress = 0f,
    ),
    CourseDto(
        id = "course-6",
        title = "آشنایی با هنر ایرانی",
        type = "PROFESSIONAL",
        instructor = "استاد رضا صالحی",
        coverUrl = "cover_tarikh_tamadon",
        description = "کاشی، مینیاتور و موسیقی؛ مسیر مقدماتی سه جلسه.",
        category = "هنر",
        isFree = true,
        sections = listOf(
            CourseSectionDto("c6s1", "نقش و معنا", 1, 25, contentType = "ARTICLE", body = "نقش اسلیمی فقط زیبایی نیست؛ تکرار منظم یعنی صبر استاد."),
            CourseSectionDto("c6s2", "رنگ در کاشی", 2, 20, contentType = "ARTICLE", body = "لاجورد و فیروزه در معماری ایرانی برای نور است، نه فقط تزئین."),
            CourseSectionDto(
                id = "c6s3",
                title = "شنیدن یک دستگاه",
                order = 3,
                durationMinutes = 27,
                contentType = "VIDEO",
                aparatUrl = "https://www.aparat.com",
                body = "یک گوشه کوتاه را بشنوید و حس آن را با یک جمله بنویسید.",
            ),
        ),
        progress = 0f,
    ),
)

private fun seedArticles(): List<ArticleDto> = listOf(
    ArticleDto("article-1", "چایخانه، نه موزه", "TEXT", "CULTURE", "سنت وقتی زنده است که محل رفت‌وآمد باشد.", "سنت را اگر فقط پشت شیشه بگذاریم، می‌میرد. چایخانه یعنی حرف زدن با صدای معمولی درباره چیزهای مهم.", null, null, "2026-08-17T08:00:00Z"),
    ArticleDto("article-2", "یک شب بدون گوشی", "TEXT", "FAMILY", "آزمایش ساده‌ای که بیشتر خانواده‌ها از آن می‌ترسند.", "گوشی‌ها را در کشو بگذارید. بازی رومیزی یا کتاب بلندخوانی. اولین بیست دقیقه سخت است؛ بعدش خانه صدا پیدا می‌کند.", null, null, "2026-08-16T18:00:00Z"),
    ArticleDto("article-3", "هفته کتاب در کتابخانه‌های محلی", "NEWS", "NEWS", "برنامه امانت ویژه و نشست قصه‌خوانی برای نوجوانان.", "کتابخانه‌های محله این هفته امانت دوم را رایگان کرده‌اند. اگر عضو نیستید، همان روز ثبت‌نام کنید.", null, null, "2026-08-18T07:30:00Z"),
    ArticleDto("article-4", "کتاب ارزان‌تر از قهوه", "TEXT", "ECONOMY", "حساب‌وکتاب کوچک برای عادت مطالعه.", "اگر هفته‌ای دو قهوه را به یک کتاب دست‌دوم تبدیل کنید، تا پایان سال یک قفسه دارید. اقتصاد فرهنگی از همین تصمیم‌های کوچک است.", null, null, "2026-08-15T11:00:00Z"),
    ArticleDto("article-5", "ادب مخالفت", "TEXT", "POLITICS", "گفت‌وگوی عمومی بدون خرد کردن طرف مقابل.", "سیاست فرهنگی یعنی بتوانیم مخالف باشیم و هنوز به یک صف نانوایی احترام بگذاریم. توهین، میان‌بر گرانی است.", null, null, "2026-08-14T09:00:00Z"),
    ArticleDto("article-6", "ده دقیقه قبل از خواب", "TEXT", "READING", "کوچک‌ترین سهمی که کتاب را زنده نگه می‌دارد.", "موبایل را یک اتاق آن‌طرف‌تر شارژ کنید. ده صفحه، نه یک جلد. فردا همان خط را پیدا می‌کنید.", null, null, "2026-08-13T21:00:00Z"),
    ArticleDto("article-7", "خط روی دیوار آشپزخانه", "TEXT", "ART", "خوشنویسی لازم نیست قاب موزه باشد.", "یک سطر نستعلیق بالای ظرف‌ها، هر روز چشم را تمرین می‌دهد. هنر وقتی در مسیر چای است، فراموش نمی‌شود.", null, null, "2026-08-12T16:00:00Z"),
    ArticleDto("article-8", "کتابخانه‌های نیشابور چه شدند؟", "TEXT", "HISTORY", "یادآوری اینکه دانش هم می‌تواند آتش بگیرد.", "سده‌ها پیش نیشابور ایستگاه کاغذ و درس بود. تاریخ را بخوانیم تا فقط به ویرانه فکر نکنیم؛ به همت بازسازی هم فکر کنیم.", null, null, "2026-08-11T10:00:00Z"),
    ArticleDto("article-9", "جلدهای کهنه مادربزرگ", "TEXT", "NARRATIVE", "بوی کاغذ و دارچین.", "مادربزرگ اسم شخصیت‌ها را عوض می‌کرد تا به نوه برسند. روایت خانوادگی اگر نوشته نشود، با یک نسل قطع می‌شود.", null, null, "2026-08-10T19:00:00Z"),
)

private fun seedAnnouncements(): List<AnnouncementDto> = listOf(
    AnnouncementDto("an-1", "مسابقه گلستان آغاز شد", "تا پایان هفته فرصت دارید در مسابقه کتاب گلستان شرکت کنید.", "2026-08-18T06:00:00Z"),
)

private fun seedContests(): List<ContestDto> = listOf(
    ContestDto("contest-1", "مسابقه گلستان", "چهار سؤال از حکایت‌های آغاز کتاب.", "BOOK", "LIVE", 4, 218, "book-1", null, "2026-09-05T20:00:00Z", null, 180, 10),
    ContestDto("contest-2", "دانستنی‌های سبک زندگی", "سؤال‌هایی از عادت خانه و مطالعه خانوادگی.", "LIFESTYLE", "LIVE", 4, 96, null, "course-2", "2026-08-30T20:00:00Z", null, 240, 15),
    ContestDto("contest-3", "اطلاعات عمومی فرهنگی", "تاریخ، هنر و کتاب؛ چهارگزینه‌ای.", "GENERAL_KNOWLEDGE", "LIVE", 4, 154, null, null, "2026-09-10T20:00:00Z", null, 300, 10),
    ContestDto("contest-4", "آزمون دوره خوشنویسی", "برای کسانی که درس کاربردی خوشنویسی را دیده‌اند.", "PRACTICAL_COURSE", "LIVE", 4, 41, null, "course-1", "2026-08-28T20:00:00Z", null, 210, 20),
    ContestDto("contest-5", "مسابقه کیمیاگر", "نتایج اعلام شد.", "BOOK", "FINISHED", 4, 310, "book-2", null, "2026-08-10T20:00:00Z", 75, 270, 10),
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
