package ir.farhangi.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import ir.farhangi.core.designsystem.icon.FarhangiIcons
import ir.farhangi.core.designsystem.theme.FarhangiHonorColors
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.LeaderboardPeriod
import ir.farhangi.core.model.RANK_FIRST
import ir.farhangi.core.model.RANK_SECOND
import ir.farhangi.core.model.RANK_THIRD
import ir.farhangi.core.model.Trophy
import ir.farhangi.core.model.honorCategoryLabel
import ir.farhangi.core.model.honorMedalLabel
import kotlinx.coroutines.launch

@Composable
fun HonorsRow(
    trophies: List<Trophy>,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            space = FarhangiSpacing.xs,
            alignment = Alignment.CenterHorizontally,
        ),
    ) {
        items(trophies, key = { it.id }) { trophy ->
            HonorMedal(trophy = trophy)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HonorMedal(
    trophy: Trophy,
    modifier: Modifier = Modifier,
) {
    val category = trophy.period.honorCategoryLabel()
    val medalLabel = honorMedalLabel(trophy.rank)
    val description = "مدال $medalLabel $category"
    val tooltipState = rememberTooltipState(isPersistent = true)
    val scope = rememberCoroutineScope()
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            TooltipAnchorPosition.Above,
        ),
        tooltip = { PlainTooltip { Text(category) } },
        state = tooltipState,
        modifier = modifier,
    ) {
        IconButton(
            onClick = { scope.launch { tooltipState.show() } },
            modifier = Modifier
                .size(FarhangiSize.touchTargetMin)
                .semantics { contentDescription = description },
        ) {
            HonorMedalFace(trophy = trophy)
        }
    }
}

@Composable
private fun HonorMedalFace(
    trophy: Trophy,
    modifier: Modifier = Modifier,
) {
    val metal = honorMetalColor(trophy.rank)
    val isWeekly = trophy.period == LeaderboardPeriod.WEEKLY
    Surface(
        modifier = modifier.size(FarhangiSize.honorMedal),
        shape = if (isWeekly) CircleShape else MaterialTheme.shapes.medium,
        color = metal.copy(alpha = MEDAL_FILL_ALPHA),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = if (isWeekly) {
                    FarhangiIcons.WeeklyMedal
                } else {
                    FarhangiIcons.MonthlyMedal
                },
                contentDescription = null,
                tint = metal,
                modifier = Modifier.size(FarhangiSize.iconLarge),
            )
        }
    }
}

private fun honorMetalColor(rank: Int): Color = when (rank) {
    RANK_FIRST -> FarhangiHonorColors.Gold
    RANK_SECOND -> FarhangiHonorColors.Silver
    RANK_THIRD -> FarhangiHonorColors.Bronze
    else -> FarhangiHonorColors.Silver
}

private const val MEDAL_FILL_ALPHA = 0.18f
