package ir.farhangi.core.network.demo

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.network.gateway.ContentGateway
import ir.farhangi.core.network.model.AnnouncementDto
import ir.farhangi.core.network.model.ArticleDto
import ir.farhangi.core.network.model.BookDto
import ir.farhangi.core.network.model.CourseDto
import ir.farhangi.core.network.model.CourseSectionDto
import ir.farhangi.core.network.model.SearchResultDto
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DemoContentGateway @Inject constructor() : ContentGateway {

    private val books = listOf(
        BookDto(
            id = "book-1",
            title = "گلستان سعدی",
            author = "سعدی شیرازی",
            categories = listOf("ادبیات کلاسیک"),
            totalPages = 320,
            rating = 4.8,
            description = "مجموعه حکایت‌های اخلاقی و تربیتی به نثر و نظم.",
        ),
        BookDto(
            id = "book-2",
            title = "کیمیاگر",
            author = "پائولو کوئیلو",
            categories = listOf("رمان", "الهام‌بخش"),
            totalPages = 210,
            rating = 4.5,
            description = "داستان سفری برای یافتن گنج و معنای زندگی.",
        ),
        BookDto(
            id = "book-3",
            title = "تاریخ تمدن ایران",
            author = "جمعی از نویسندگان",
            categories = listOf("تاریخ", "فرهنگ"),
            totalPages = 450,
            rating = 4.6,
            description = "مروری بر تمدن و فرهنگ ایران در دوره‌های مختلف.",
        ),
    )

    private val courses = listOf(
        CourseDto(
            id = "course-1",
            title = "آشنایی با خوشنویسی",
            type = "PRACTICAL",
            description = "درس کوتاه برای شروع خوشنویسی نستعلیق.",
            sections = listOf(
                CourseSectionDto("s1", "ابزار و مقدمات", 1, 15),
                CourseSectionDto("s2", "حروف پایه", 2, 20),
            ),
            progress = 0.3f,
        ),
        CourseDto(
            id = "course-2",
            title = "مسیر ادبیات معاصر",
            type = "PROFESSIONAL",
            description = "مسیر یادگیری ساختارمند ادبیات معاصر فارسی.",
            sections = listOf(
                CourseSectionDto("s1", "مقدمات", 1, 30),
                CourseSectionDto("s2", "شعر معاصر", 2, 45),
                CourseSectionDto("s3", "نثر معاصر", 3, 45),
            ),
            progress = 0.1f,
        ),
    )

    private val articles = listOf(
        ArticleDto(
            id = "article-1",
            title = "نقش کتابخوانی در خانواده",
            type = "TEXT",
            category = "سبک زندگی",
            summary = "چگونه عادت مطالعه را در خانواده تقویت کنیم.",
            body = "مطالعه مشترک خانواده یکی از مؤثرترین راه‌های انتقال فرهنگ است...",
            publishedAt = "2026-07-01T10:00:00Z",
        ),
        ArticleDto(
            id = "article-2",
            title = "مستند: میراث فرهنگی",
            type = "VIDEO",
            category = "مستند",
            summary = "نگاهی کوتاه به میراث فرهنگی ایران.",
            mediaUrl = "https://example.com/video/heritage",
            publishedAt = "2026-07-15T12:00:00Z",
        ),
        ArticleDto(
            id = "article-3",
            title = "پادکست: داستان کوتاه شب",
            type = "PODCAST",
            category = "صوتی",
            summary = "روایت یک داستان کوتاه برای پیش از خواب.",
            mediaUrl = "https://example.com/audio/night-story",
            publishedAt = "2026-07-20T18:00:00Z",
        ),
        ArticleDto(
            id = "article-4",
            title = "خبر: هفته کتاب فرهنگی",
            type = "NEWS",
            category = "اخبار",
            summary = "برنامه‌های هفته کتاب در مراکز فرهنگی.",
            body = "هفته کتاب با نشست‌های معرفی و کارگاه‌های خانواده برگزار می‌شود.",
            publishedAt = "2026-07-28T09:00:00Z",
        ),
    )

    private val announcements = listOf(
        AnnouncementDto(
            id = "ann-1",
            title = "به‌روزرسانی کتابخانه",
            body = "مجموعه تازه‌ای از کتاب‌های ادبیات کلاسیک اضافه شد.",
            publishedAt = "2026-07-25T08:00:00Z",
        ),
        AnnouncementDto(
            id = "ann-2",
            title = "دوره جدید خوشنویسی",
            body = "ثبت‌نام مسیر یادگیری خوشنویسی از این هفته آغاز شده است.",
            publishedAt = "2026-07-27T08:00:00Z",
        ),
    )

    override suspend fun getBooks(query: String?): Result<List<BookDto>> {
        delay(NETWORK_DELAY_MS)
        val filtered = books.filterByQuery(query) { it.title + it.author }
        return Result.Success(filtered)
    }

    override suspend fun getBook(id: String): Result<BookDto> {
        delay(NETWORK_DELAY_MS)
        return books.find { it.id == id }?.let { Result.Success(it) }
            ?: Result.Error(NoSuchElementException("کتاب یافت نشد"))
    }

    override suspend fun getCourses(query: String?): Result<List<CourseDto>> {
        delay(NETWORK_DELAY_MS)
        val filtered = courses.filterByQuery(query) { it.title }
        return Result.Success(filtered)
    }

    override suspend fun getCourse(id: String): Result<CourseDto> {
        delay(NETWORK_DELAY_MS)
        return courses.find { it.id == id }?.let { Result.Success(it) }
            ?: Result.Error(NoSuchElementException("دوره یافت نشد"))
    }

    override suspend fun getArticles(query: String?): Result<List<ArticleDto>> {
        delay(NETWORK_DELAY_MS)
        val filtered = articles.filterByQuery(query) { it.title + it.category }
        return Result.Success(filtered)
    }

    override suspend fun getArticle(id: String): Result<ArticleDto> {
        delay(NETWORK_DELAY_MS)
        return articles.find { it.id == id }?.let { Result.Success(it) }
            ?: Result.Error(NoSuchElementException("مقاله یافت نشد"))
    }

    override suspend fun getAnnouncements(): Result<List<AnnouncementDto>> {
        delay(NETWORK_DELAY_MS)
        return Result.Success(announcements)
    }

    override suspend fun search(query: String, type: String?): Result<List<SearchResultDto>> {
        delay(NETWORK_DELAY_MS)
        if (query.isBlank()) return Result.Success(emptyList())
        val q = query.trim()
        val bookResults = books
            .filter { it.title.contains(q, ignoreCase = true) || it.author.contains(q, ignoreCase = true) }
            .map { SearchResultDto(it.id, it.title, it.author, "BOOK", it.coverUrl) }
        val courseResults = courses
            .filter { it.title.contains(q, ignoreCase = true) }
            .map { SearchResultDto(it.id, it.title, it.type, "COURSE", it.coverUrl) }
        val articleResults = articles
            .filter { it.title.contains(q, ignoreCase = true) }
            .map { SearchResultDto(it.id, it.title, it.category, it.type, it.coverUrl) }
        val all = (bookResults + courseResults + articleResults).filter {
            type == null || it.type.equals(type, ignoreCase = true)
        }
        return Result.Success(all)
    }

    private fun <T> List<T>.filterByQuery(query: String?, text: (T) -> String): List<T> {
        if (query.isNullOrBlank()) return this
        return filter { text(it).contains(query.trim(), ignoreCase = true) }
    }

    companion object {
        private const val NETWORK_DELAY_MS = 300L
    }
}