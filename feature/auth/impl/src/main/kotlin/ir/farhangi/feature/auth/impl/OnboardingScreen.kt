package ir.farhangi.feature.auth.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
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
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = item.title, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(FarhangiSpacing.md))
                Text(
                    text = item.body,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Text(
            text = "${pagerState.currentPage + 1} از ${PAGES.size}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally),
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

private data class OnboardingPage(val title: String, val body: String)

private val PAGES = listOf(
    OnboardingPage("کتابخانه همیشه همراه", "هزاران کتاب را آنلاین بخوانید و در کتاب‌خانه من ذخیره کنید."),
    OnboardingPage("آموزش به دو شیوه", "دوره‌های تخصصی چندجلسه و دوره‌های کاربردی تک‌محتوا، فعلاً رایگان."),
    OnboardingPage("مسابقه و هم‌خوان", "در آزمون‌ها شرکت کنید و با مطالعه سالم در جدول هفته و ماه رقابت کنید."),
    OnboardingPage("مجله روزانه", "خبر، روایت و مقاله فرهنگی برای خانواده و جامعه."),
)
