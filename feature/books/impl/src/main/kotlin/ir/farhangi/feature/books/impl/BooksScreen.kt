package ir.farhangi.feature.books.impl

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
fun BooksScreen(
    onLibraryClick: () -> Unit,
    onMyLibraryClick: () -> Unit,
    onContestsClick: () -> Unit,
    onHamkhanClick: () -> Unit,
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
                title = "کتابخانه",
                subtitle = "آرشیو موضوعی و مطالعه آنلاین",
                icon = FarhangiIcons.Books,
                onClick = onLibraryClick,
            )
        }
        item {
            HubTile(
                title = "کتاب‌خانه من",
                subtitle = "کتاب‌های ذخیره‌شده برای بعد",
                icon = FarhangiIcons.BooksOutlined,
                onClick = onMyLibraryClick,
            )
        }
        item {
            HubTile(
                title = "مسابقات کتاب",
                subtitle = "آزمون‌های در جریان و نتایج",
                icon = FarhangiIcons.Competitions,
                onClick = onContestsClick,
            )
        }
        item {
            HubTile(
                title = "هم‌خوان",
                subtitle = "باشگاه مطالعه، جدول و جام",
                icon = FarhangiIcons.Trophy,
                onClick = onHamkhanClick,
            )
        }
    }
}
