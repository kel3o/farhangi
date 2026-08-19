package ir.farhangi.feature.competitions.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.ContestStatus
import ir.farhangi.core.model.persianLabel
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState

@Composable
fun ContestDetailScreen(
    uiState: ContestDetailUiState,
    onStartQuiz: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        ContestDetailUiState.Loading -> LoadingState(modifier)
        is ContestDetailUiState.Error -> EmptyState("خطا", uiState.message, modifier)
        is ContestDetailUiState.Success -> {
            val contest = uiState.contest
            Column(
                modifier = modifier.fillMaxSize().padding(FarhangiSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
            ) {
                Text(contest.category.persianLabel(), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Text(contest.title, style = MaterialTheme.typography.headlineSmall)
                Text(contest.summary, style = MaterialTheme.typography.bodyLarge)
                Text("${contest.questionCount} سؤال چهارگزینه‌ای · ${contest.status.persianLabel()}", style = MaterialTheme.typography.bodyMedium)
                contest.userScorePercent?.let {
                    Text("نتیجه ثبت‌شده شما: $it درصد", style = MaterialTheme.typography.titleSmall)
                }
                if (contest.status == ContestStatus.LIVE) {
                    Button(
                        onClick = onStartQuiz,
                        modifier = Modifier.fillMaxWidth().heightIn(min = FarhangiSize.touchTargetMin),
                    ) { Text("شروع آزمون") }
                }
                Button(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth().heightIn(min = FarhangiSize.touchTargetMin),
                ) { Text("بازگشت") }
            }
        }
    }
}
