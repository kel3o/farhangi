package ir.farhangi.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Fixed 4dp grid spacing tokens — no magic numbers in UI.
 */
@Immutable
object FarhangiSpacing {
    val xxs: Dp = 4.dp
    val xs: Dp = 8.dp
    val sm: Dp = 12.dp
    val md: Dp = 16.dp
    val lg: Dp = 24.dp
    val xl: Dp = 32.dp
}

object FarhangiSize {
    val touchTargetMin: Dp = 48.dp
    val iconDefault: Dp = 24.dp
    val iconSmall: Dp = 20.dp
    val coverWidth: Dp = 96.dp
    val coverHeight: Dp = 144.dp
    val avatar: Dp = 72.dp
    val avatarSmall: Dp = 40.dp
    val iconLarge: Dp = 32.dp
    val chartHeight: Dp = 160.dp
    val barMinWidth: Dp = 16.dp
}