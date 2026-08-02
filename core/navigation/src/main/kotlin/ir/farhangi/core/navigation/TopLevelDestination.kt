package ir.farhangi.core.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey

data class TopLevelDestination(
    val key: NavKey,
    val labelResId: Int,
    val contentDescriptionResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)