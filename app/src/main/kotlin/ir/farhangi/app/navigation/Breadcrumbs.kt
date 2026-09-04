package ir.farhangi.app.navigation

import androidx.navigation3.runtime.NavKey
import ir.farhangi.feature.auth.api.NotificationPermissionRoute
import ir.farhangi.feature.auth.api.OnboardingRoute
import ir.farhangi.feature.auth.api.OtpRoute
import ir.farhangi.feature.auth.api.PhoneRoute
import ir.farhangi.feature.books.api.BookContestsRoute
import ir.farhangi.feature.books.api.BookDetailRoute
import ir.farhangi.feature.books.api.BookReaderRoute
import ir.farhangi.feature.books.api.BooksRoute
import ir.farhangi.feature.books.api.HamkhanRoute
import ir.farhangi.feature.books.api.LibraryRoute
import ir.farhangi.feature.books.api.MyLibraryRoute
import ir.farhangi.feature.competitions.api.CompetitionsRoute
import ir.farhangi.feature.competitions.api.ContestDetailRoute
import ir.farhangi.feature.competitions.api.QuizRoute
import ir.farhangi.feature.courses.api.CourseDetailRoute
import ir.farhangi.feature.courses.api.CoursesRoute
import ir.farhangi.feature.courses.api.LessonRoute
import ir.farhangi.feature.courses.api.PracticalCatalogRoute
import ir.farhangi.feature.courses.api.ProfessionalCatalogRoute
import ir.farhangi.feature.home.api.HomeRoute
import ir.farhangi.feature.magazine.api.ArticleDetailRoute
import ir.farhangi.feature.magazine.api.MagazineRoute
import ir.farhangi.feature.profile.api.ProfileRoute
import ir.farhangi.feature.search.api.SearchRoute
import ir.farhangi.feature.studio.api.StudioArticleEditorRoute
import ir.farhangi.feature.studio.api.StudioArticlesRoute
import ir.farhangi.feature.studio.api.StudioBookEditorRoute
import ir.farhangi.feature.studio.api.StudioBooksRoute
import ir.farhangi.feature.studio.api.StudioContestEditorRoute
import ir.farhangi.feature.studio.api.StudioContestStatsRoute
import ir.farhangi.feature.studio.api.StudioContestsRoute
import ir.farhangi.feature.studio.api.StudioCourseEditorRoute
import ir.farhangi.feature.studio.api.StudioCoursesRoute
import ir.farhangi.feature.studio.api.OrgInboxRoute
import ir.farhangi.feature.studio.api.ReportsRoute
import ir.farhangi.feature.studio.api.RolesRoute
import ir.farhangi.feature.studio.api.StudioHomeRoute

private const val BREADCRUMB_SEPARATOR = " › "

fun breadcrumbTitle(backStack: List<NavKey>): String {
    if (backStack.isEmpty()) return "خانه"
    val labels = backStack.mapNotNull { it.breadcrumbLabel() }
    return labels.joinToString(BREADCRUMB_SEPARATOR).ifBlank { "خانه" }
}

private fun NavKey.breadcrumbLabel(): String? = when (this) {
    is HomeRoute -> "خانه"
    is BooksRoute -> "کتاب"
    is LibraryRoute -> "کتابخانه"
    is MyLibraryRoute -> "کتابخانه من"
    is BookContestsRoute -> "مسابقات کتاب"
    is HamkhanRoute -> "هم‌خوان"
    is BookDetailRoute -> categoryLabel.ifBlank { "جزئیات" }
    is BookReaderRoute -> categoryLabel.ifBlank { "مطالعه" }
    is CoursesRoute -> "آموزش"
    is ProfessionalCatalogRoute -> "تخصصی"
    is PracticalCatalogRoute -> "کاربردی"
    is CourseDetailRoute -> "جزئیات دوره"
    is LessonRoute -> "جلسه"
    is MagazineRoute -> "مجله"
    is ArticleDetailRoute -> "مقاله"
    is CompetitionsRoute -> "مسابقه"
    is ContestDetailRoute -> "جزئیات مسابقه"
    is QuizRoute -> "آزمون"
    is ProfileRoute -> "پروفایل"
    is SearchRoute -> "جستجو"
    is StudioHomeRoute -> "استودیو"
    is StudioBooksRoute -> "کتاب‌ها"
    is StudioBookEditorRoute -> if (bookId.isBlank()) "کتاب جدید" else "ویرایش کتاب"
    is StudioCoursesRoute -> "آموزش‌ها"
    is StudioCourseEditorRoute -> if (courseId.isBlank()) "دوره جدید" else "ویرایش دوره"
    is StudioArticlesRoute -> "مجله"
    is StudioArticleEditorRoute -> if (articleId.isBlank()) "مطلب جدید" else "ویرایش مطلب"
    is StudioContestsRoute -> "مسابقه‌ها"
    is StudioContestEditorRoute -> if (contestId.isBlank()) "مسابقه جدید" else "ویرایش مسابقه"
    is StudioContestStatsRoute -> "آمار مسابقه"
    is OrgInboxRoute -> "پیام‌ها"
    is ReportsRoute -> "گزارش‌ها"
    is RolesRoute -> "نقش‌ها"
    is OnboardingRoute,
    is PhoneRoute,
    is OtpRoute,
    is NotificationPermissionRoute,
    -> null
    else -> null
}
