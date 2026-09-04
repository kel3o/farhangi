package ir.farhangi.core.ui

import android.graphics.Paint
import android.graphics.Typeface
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import ir.farhangi.core.designsystem.theme.FarhangiHonorColors
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.model.LeaderboardPeriod
import ir.farhangi.core.model.RANK_FIRST
import ir.farhangi.core.model.RANK_SECOND
import ir.farhangi.core.model.RANK_THIRD

@Composable
fun RankMedal(
    rank: Int,
    period: LeaderboardPeriod,
    modifier: Modifier = Modifier,
) {
    val metal = honorMetalColor(rank)
    val numeral = honorNumeralColor(rank)
    val isWeekly = period == LeaderboardPeriod.WEEKLY
    val safeRank = rank.coerceAtLeast(1)
    Canvas(
        modifier = modifier.size(
            width = FarhangiSize.honorMedal,
            height = FarhangiSize.honorMedalTall,
        ),
    ) {
        if (isWeekly) {
            drawWeeklyMedal(metal = metal, rank = safeRank, numeral = numeral)
        } else {
            drawMonthlyMedal(metal = metal, rank = safeRank, numeral = numeral)
        }
    }
}

internal fun honorMetalColor(rank: Int): Color = when (rank) {
    RANK_FIRST -> FarhangiHonorColors.Gold
    RANK_SECOND -> FarhangiHonorColors.Silver
    RANK_THIRD -> FarhangiHonorColors.Bronze
    else -> FarhangiHonorColors.Silver
}

private fun honorNumeralColor(rank: Int): Color = when (rank) {
    RANK_FIRST -> FarhangiHonorColors.GoldDeep
    RANK_SECOND -> FarhangiHonorColors.SilverDeep
    RANK_THIRD -> FarhangiHonorColors.BronzeDeep
    else -> FarhangiHonorColors.SilverDeep
}

private fun DrawScope.drawWeeklyMedal(metal: Color, rank: Int, numeral: Color) {
    val medalRadius = size.minDimension * WEEKLY_MEDAL_RADIUS_RATIO
    val center = Offset(size.width / 2f, size.height * WEEKLY_CENTER_Y_RATIO)
    drawVRibbon(
        color = FarhangiHonorColors.WeeklyRibbon,
        apex = Offset(center.x, size.height * WEEKLY_RIBBON_APEX_RATIO),
        hang = size.height * WEEKLY_RIBBON_HANG_RATIO,
        spread = size.width * RIBBON_SPREAD_RATIO,
    )
    drawCircle(color = metal, radius = medalRadius, center = center)
    drawCircle(
        color = metal.copy(alpha = RING_ALPHA),
        radius = medalRadius * INNER_RING_RATIO,
        center = center,
        style = Stroke(width = size.minDimension * RING_STROKE_RATIO),
    )
    drawRankDigit(rank = rank, center = center, medalRadius = medalRadius, color = numeral)
}

private fun DrawScope.drawMonthlyMedal(metal: Color, rank: Int, numeral: Color) {
    val medalRadius = size.minDimension * MONTHLY_MEDAL_RADIUS_RATIO
    val center = Offset(size.width / 2f, size.height * MONTHLY_CENTER_Y_RATIO)
    drawCircle(color = metal, radius = medalRadius, center = center)
    drawWreath(center = center, radius = medalRadius * WREATH_RADIUS_RATIO)
    drawRankDigit(rank = rank, center = center, medalRadius = medalRadius, color = numeral)
    drawVRibbon(
        color = FarhangiHonorColors.MonthlyRibbon,
        apex = Offset(center.x, center.y + medalRadius * RIBBON_ATTACH_RATIO),
        hang = size.height * MONTHLY_RIBBON_HANG_RATIO,
        spread = size.width * RIBBON_SPREAD_RATIO,
    )
}

private fun DrawScope.drawRankDigit(
    rank: Int,
    center: Offset,
    medalRadius: Float,
    color: Color,
) {
    val paint = Paint().apply {
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        textSize = medalRadius * DIGIT_SIZE_RATIO
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        this.color = color.toArgb()
    }
    val baseline = center.y - (paint.ascent() + paint.descent()) / 2f
    drawContext.canvas.nativeCanvas.drawText(
        rank.toString(),
        center.x,
        baseline,
        paint,
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

private const val WEEKLY_MEDAL_RADIUS_RATIO = 0.34f
private const val MONTHLY_MEDAL_RADIUS_RATIO = 0.36f
private const val WEEKLY_CENTER_Y_RATIO = 0.58f
private const val MONTHLY_CENTER_Y_RATIO = 0.42f
private const val WEEKLY_RIBBON_APEX_RATIO = 0.08f
private const val WEEKLY_RIBBON_HANG_RATIO = 0.28f
private const val MONTHLY_RIBBON_HANG_RATIO = 0.22f
private const val RIBBON_SPREAD_RATIO = 0.22f
private const val RIBBON_INNER_RATIO = 0.45f
private const val RIBBON_NOTCH_RATIO = 0.72f
private const val RIBBON_ATTACH_RATIO = 0.72f
private const val INNER_RING_RATIO = 0.78f
private const val RING_STROKE_RATIO = 0.04f
private const val RING_ALPHA = 0.55f
private const val RING_DOT_RATIO = 0.10f
private const val WREATH_RADIUS_RATIO = 0.82f
private const val WREATH_STROKE_RATIO = 0.05f
private const val WREATH_START_DEG = 50f
private const val WREATH_SWEEP_DEG = 80f
private const val WREATH_MIRROR_OFFSET_DEG = 180f
private const val DIGIT_SIZE_RATIO = 1.05f
