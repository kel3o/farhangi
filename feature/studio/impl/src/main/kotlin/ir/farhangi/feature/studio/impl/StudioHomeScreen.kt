package ir.farhangi.feature.studio.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ir.farhangi.core.designsystem.icon.FarhangiIcons
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.ui.HubTile

@Composable
fun StudioHomeScreen(
    onCreateBook: () -> Unit,
    onCreateCourse: () -> Unit,
    onCreateArticle: () -> Unit,
    onCreateContest: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = FarhangiSpacing.md,
            end = FarhangiSpacing.md,
            top = contentPadding.calculateTopPadding() + FarhangiSpacing.sm,
            bottom = contentPadding.calculateBottomPadding() + FarhangiSpacing.lg,
        ),
        verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
    ) {
        item {
            HubTile("کتاب تازه", "افزودن یا ویرایش کتاب", FarhangiIcons.Books, onCreateBook)
        }
        item {
            HubTile("آموزش تازه", "دوره تخصصی یا کاربردی", FarhangiIcons.Courses, onCreateCourse)
        }
        item {
            HubTile("یادداشت مجله", "انتشار مطلب روزانه", FarhangiIcons.Magazine, onCreateArticle)
        }
        item {
            HubTile("مسابقه تازه", "تعریف آزمون چهارگزینه‌ای", FarhangiIcons.Competitions, onCreateContest)
        }
    }
}
