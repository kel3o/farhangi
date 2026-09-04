package ir.farhangi.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import ir.farhangi.core.designsystem.theme.FarhangiHonorColors
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.model.LeaderboardPeriod
import ir.farhangi.core.model.RANK_FIRST
import ir.farhangi.core.model.RANK_SECOND
import ir.farhangi.core.model.RANK_THIRD
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RankMedal(
    rank: Int,
    period: LeaderboardPeriod,
    modifier: Modifier = Modifier,
) {
    val metal = honorMetalColor(rank)
    val rim = honorRimColor(rank)
    val isWeekly = period == LeaderboardPeriod.WEEKLY
    Canvas(
        modifier = modifier.size(
            width = FarhangiSize.honorMedal,
            height = FarhangiSize.honorMedalTall,
        ),
    ) {
        if (isWeekly) {
            drawWeeklyMedal(metal = metal, rim = rim)
        } else {
            drawMonthlyMedal(metal = metal, rim = rim)
        }
    }
}

internal fun honorMetalColor(rank: Int): Color = when (rank) {
    RANK_FIRST -> FarhangiHonorColors.Gold
    RANK_SECOND -> FarhangiHonorColors.Silver
    RANK_THIRD -> FarhangiHonorColors.Bronze
    else -> FarhangiHonorColors.Silver
}

private fun honorRimColor(rank: Int): Color = when (rank) {
    RANK_FIRST -> FarhangiHonorColors.GoldDeep
    RANK_SECOND -> FarhangiHonorColors.SilverDeep
    RANK_THIRD -> FarhangiHonorColors.BronzeDeep
    else -> FarhangiHonorColors.SilverDeep
}

private fun DrawScope.drawWeeklyMedal(metal: Color, rim: Color) {
    val medalRadius = size.minDimension * WEEKLY_MEDAL_RADIUS_RATIO
    val center = Offset(size.width / 2f, size.height * WEEKLY_CENTER_Y_RATIO)
    drawVRibbon(
        color = FarhangiHonorColors.WeeklyRibbon,
        apex = Offset(center.x, size.height * WEEKLY_RIBBON_APEX_RATIO),
        hang = size.height * WEEKLY_RIBBON_HANG_RATIO,
        spread = size.width * RIBBON_SPREAD_RATIO,
    )
    drawCircle(color = rim, radius = medalRadius, center = center)
    drawCircle(
        color = metal,
        radius = medalRadius * INNER_DISC_RATIO,
        center = center,
    )
    drawMetallicSheen(
        center = Offset(
            center.x - medalRadius * SHEEN_OFFSET_RATIO,
            center.y - medalRadius * SHEEN_OFFSET_RATIO,
        ),
        radius = medalRadius * SHEEN_RADIUS_RATIO,
    )
    drawCircle(
        color = metal.highlight(),
        radius = medalRadius * INNER_RING_RATIO,
        center = center,
        style = Stroke(width = size.minDimension * RING_STROKE_RATIO),
    )
    drawPath(
        path = starPath(
            center = center,
            outerRadius = medalRadius * STAR_OUTER_RATIO,
            innerRadius = medalRadius * STAR_INNER_RATIO,
            points = STAR_POINTS,
        ),
        color = FarhangiHonorColors.OnMedal,
        style = Fill,
    )
}

private fun DrawScope.drawMonthlyMedal(metal: Color, rim: Color) {
    val medalRadius = size.minDimension * MONTHLY_MEDAL_RADIUS_RATIO
    val center = Offset(size.width / 2f, size.height * MONTHLY_CENTER_Y_RATIO)
    drawWreath(center = center, radius = medalRadius * WREATH_RADIUS_RATIO)
    drawPath(
        path = hexagonPath(center = center, radius = medalRadius),
        color = rim,
    )
    drawPath(
        path = hexagonPath(center = center, radius = medalRadius * INNER_DISC_RATIO),
        color = metal,
    )
    drawMetallicSheen(
        center = Offset(
            center.x - medalRadius * SHEEN_OFFSET_RATIO,
            center.y - medalRadius * SHEEN_OFFSET_RATIO,
        ),
        radius = medalRadius * SHEEN_RADIUS_RATIO,
    )
    drawPath(
        path = hexagonPath(center = center, radius = medalRadius * INNER_RING_RATIO),
        color = metal.highlight(),
        style = Stroke(width = size.minDimension * RING_STROKE_RATIO),
    )
    drawPath(
        path = diamondPath(center = center, radius = medalRadius * DIAMOND_RADIUS_RATIO),
        color = FarhangiHonorColors.OnMedal,
        style = Fill,
    )
    drawVRibbon(
        color = FarhangiHonorColors.MonthlyRibbon,
        apex = Offset(center.x, center.y + medalRadius * RIBBON_ATTACH_RATIO),
        hang = size.height * MONTHLY_RIBBON_HANG_RATIO,
        spread = size.width * RIBBON_SPREAD_RATIO,
    )
}

private fun DrawScope.drawMetallicSheen(center: Offset, radius: Float) {
    drawCircle(
        color = Color.White.copy(alpha = SHEEN_ALPHA),
        radius = radius,
        center = center,
    )
}

private fun DrawScope.drawVRibbon(
    color: Color,
    apex: Offset,
    hang: Float,
    spread: Float,
) {
    val left = Path().apply {
        moveTo(apex.x, apex.y)
        lineTo(apex.x - spread, apex.y + hang)
        lineTo(apex.x - spread * RIBBON_INNER_RATIO, apex.y + hang * RIBBON_NOTCH_RATIO)
        close()
    }
    val right = Path().apply {
        moveTo(apex.x, apex.y)
        lineTo(apex.x + spread, apex.y + hang)
        lineTo(apex.x + spread * RIBBON_INNER_RATIO, apex.y + hang * RIBBON_NOTCH_RATIO)
        close()
    }
    drawPath(left, color)
    drawPath(right, color)
    val ringSize = size.minDimension * RING_DOT_RATIO
    drawRoundRect(
        color = color,
        topLeft = Offset(apex.x - ringSize / 2f, apex.y - ringSize / 2f),
        size = Size(ringSize, ringSize),
        cornerRadius = CornerRadius(ringSize / 2f, ringSize / 2f),
    )
}

private fun DrawScope.drawWreath(center: Offset, radius: Float) {
    val stroke = Stroke(
        width = size.minDimension * WREATH_STROKE_RATIO,
        cap = StrokeCap.Round,
    )
    drawArc(
        color = FarhangiHonorColors.Wreath,
        startAngle = WREATH_START_DEG,
        sweepAngle = WREATH_SWEEP_DEG,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(radius * 2f, radius * 2f),
        style = stroke,
    )
    drawArc(
        color = FarhangiHonorColors.Wreath,
        startAngle = WREATH_START_DEG + WREATH_MIRROR_OFFSET_DEG,
        sweepAngle = WREATH_SWEEP_DEG,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(radius * 2f, radius * 2f),
        style = stroke,
    )
}

private fun starPath(
    center: Offset,
    outerRadius: Float,
    innerRadius: Float,
    points: Int,
): Path {
    val path = Path()
    val step = PI / points
    var angle = -PI / 2
    repeat(points * 2) { index ->
        val radius = if (index % 2 == 0) outerRadius else innerRadius
        val x = center.x + (cos(angle) * radius).toFloat()
        val y = center.y + (sin(angle) * radius).toFloat()
        if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        angle += step
    }
    path.close()
    return path
}

private fun hexagonPath(center: Offset, radius: Float): Path {
    val path = Path()
    repeat(HEXAGON_SIDES) { index ->
        val angle = Math.toRadians((HEXAGON_STEP_DEG * index - HEXAGON_OFFSET_DEG).toDouble())
        val x = center.x + (cos(angle) * radius).toFloat()
        val y = center.y + (sin(angle) * radius).toFloat()
        if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

private fun diamondPath(center: Offset, radius: Float): Path = Path().apply {
    moveTo(center.x, center.y - radius)
    lineTo(center.x + radius * DIAMOND_WIDTH_RATIO, center.y)
    lineTo(center.x, center.y + radius)
    lineTo(center.x - radius * DIAMOND_WIDTH_RATIO, center.y)
    close()
}

private fun Color.highlight(): Color = lerp(this, Color.White, HIGHLIGHT_MIX)

private const val WEEKLY_MEDAL_RADIUS_RATIO = 0.36f
private const val MONTHLY_MEDAL_RADIUS_RATIO = 0.34f
private const val WEEKLY_CENTER_Y_RATIO = 0.58f
private const val MONTHLY_CENTER_Y_RATIO = 0.42f
private const val WEEKLY_RIBBON_APEX_RATIO = 0.08f
private const val WEEKLY_RIBBON_HANG_RATIO = 0.28f
private const val MONTHLY_RIBBON_HANG_RATIO = 0.22f
private const val RIBBON_SPREAD_RATIO = 0.22f
private const val RIBBON_INNER_RATIO = 0.45f
private const val RIBBON_NOTCH_RATIO = 0.72f
private const val RIBBON_ATTACH_RATIO = 0.78f
private const val INNER_DISC_RATIO = 0.86f
private const val INNER_RING_RATIO = 0.70f
private const val RING_STROKE_RATIO = 0.045f
private const val RING_DOT_RATIO = 0.10f
private const val WREATH_RADIUS_RATIO = 1.08f
private const val WREATH_STROKE_RATIO = 0.055f
private const val WREATH_START_DEG = 40f
private const val WREATH_SWEEP_DEG = 100f
private const val WREATH_MIRROR_OFFSET_DEG = 180f
private const val STAR_OUTER_RATIO = 0.42f
private const val STAR_INNER_RATIO = 0.18f
private const val STAR_POINTS = 5
private const val DIAMOND_RADIUS_RATIO = 0.28f
private const val DIAMOND_WIDTH_RATIO = 0.62f
private const val HEXAGON_SIDES = 6
private const val HEXAGON_STEP_DEG = 60f
private const val HEXAGON_OFFSET_DEG = 30f
private const val SHEEN_OFFSET_RATIO = 0.22f
private const val SHEEN_RADIUS_RATIO = 0.32f
private const val SHEEN_ALPHA = 0.28f
private const val HIGHLIGHT_MIX = 0.38f
