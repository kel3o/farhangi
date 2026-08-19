package ir.farhangi.feature.courses.impl

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
fun CoursesScreen(
    onProfessionalClick: () -> Unit,
    onPracticalClick: () -> Unit,
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
            HubTile(
                title = "دوره‌های تخصصی",
                subtitle = "چند جلسه، مسیر یادگیری و درصد پیشرفت",
                icon = FarhangiIcons.Courses,
                onClick = onProfessionalClick,
            )
        }
        item {
            HubTile(
                title = "دوره‌های کاربردی",
                subtitle = "یک محتوا، موضوع‌های متنوع",
                icon = FarhangiIcons.CoursesOutlined,
                onClick = onPracticalClick,
            )
        }
    }
}
