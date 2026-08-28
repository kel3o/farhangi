package ir.farhangi.feature.auth.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import ir.farhangi.core.designsystem.icon.FarhangiIcons
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.toPersianDigits
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { PAGES.size })
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(FarhangiSpacing.lg),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
        ) { page ->
            val item = PAGES[page]
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(
                    FarhangiSpacing.md,
                    Alignment.CenterVertically,
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    modifier = Modifier.size(FarhangiSize.avatar),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = item.body,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        Text(
            text = "${(pagerState.currentPage + 1).toPersianDigits()} از ${PAGES.size.toPersianDigits()}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(FarhangiSpacing.md))
        Button(
            onClick = {
                if (pagerState.currentPage == PAGES.lastIndex) {
                    onFinished()
                } else {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = FarhangiSize.touchTargetMin),
        ) {
            Text(if (pagerState.currentPage == PAGES.lastIndex) "شروع" else "بعدی")
        }
        TextButton(
            onClick = onFinished,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = FarhangiSize.touchTargetMin),
        ) {
            Text("رد کردن")
        }
    }
}

private data class OnboardingPage(
    val title: String,
    val body: String,
    val icon: ImageVector,
)

private val PAGES = listOf(
    OnboardingPage(
        title = "کتابخانه همیشه همراه",
        body = "هزاران کتاب را آنلاین بخوانید و در کتاب‌خانه من ذخیره کنید.",
        icon = FarhangiIcons.Books,
    ),
    OnboardingPage(
        title = "آموزش به دو شیوه",
        body = "دوره‌های تخصصی چندجلسه و دوره‌های کاربردی تک‌محتوا، فعلاً رایگان.",
        icon = FarhangiIcons.Courses,
    ),
    OnboardingPage(
        title = "مسابقه و هم‌خوان",
        body = "در آزمون‌ها شرکت کنید و با مطالعه سالم در جدول هفته و ماه رقابت کنید.",
        icon = FarhangiIcons.Competitions,
    ),
    OnboardingPage(
        title = "مجله روزانه",
        body = "خبر، روایت و مقاله فرهنگی برای خانواده و جامعه.",
        icon = FarhangiIcons.Magazine,
    ),
)
