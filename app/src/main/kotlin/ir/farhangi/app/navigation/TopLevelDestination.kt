package ir.farhangi.app.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import ir.farhangi.app.R
import ir.farhangi.core.designsystem.icon.FarhangiIcons
import ir.farhangi.feature.books.api.BooksRoute
import ir.farhangi.feature.competitions.api.CompetitionsRoute
import ir.farhangi.feature.courses.api.CoursesRoute
import ir.farhangi.feature.home.api.HomeRoute
import ir.farhangi.feature.magazine.api.MagazineRoute

enum class TopLevelDestination(
    val route: NavKey,
    val labelResId: Int,
    val contentDescriptionResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    HOME(
        route = HomeRoute,
        labelResId = R.string.nav_home,
        contentDescriptionResId = R.string.nav_home,
        selectedIcon = FarhangiIcons.Home,
        unselectedIcon = FarhangiIcons.HomeOutlined,
    ),
    BOOKS(
        route = BooksRoute,
        labelResId = R.string.nav_books,
        contentDescriptionResId = R.string.nav_books,
        selectedIcon = FarhangiIcons.Books,
        unselectedIcon = FarhangiIcons.BooksOutlined,
    ),
    COURSES(
        route = CoursesRoute,
        labelResId = R.string.nav_courses,
        contentDescriptionResId = R.string.nav_courses,
        selectedIcon = FarhangiIcons.Courses,
        unselectedIcon = FarhangiIcons.CoursesOutlined,
    ),
    COMPETITIONS(
        route = CompetitionsRoute,
        labelResId = R.string.nav_competitions,
        contentDescriptionResId = R.string.nav_competitions,
        selectedIcon = FarhangiIcons.Competitions,
        unselectedIcon = FarhangiIcons.CompetitionsOutlined,
    ),
    MAGAZINE(
        route = MagazineRoute,
        labelResId = R.string.nav_magazine,
        contentDescriptionResId = R.string.nav_magazine,
        selectedIcon = FarhangiIcons.Magazine,
        unselectedIcon = FarhangiIcons.MagazineOutlined,
    ),
}
