package ir.farhangi.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val LightColorScheme = lightColorScheme(
    primary = FarhangiPalette.PrimaryLight,
    onPrimary = FarhangiPalette.OnPrimaryLight,
    primaryContainer = FarhangiPalette.PrimaryContainerLight,
    onPrimaryContainer = FarhangiPalette.OnPrimaryContainerLight,
    secondary = FarhangiPalette.SecondaryLight,
    onSecondary = FarhangiPalette.OnSecondaryLight,
    secondaryContainer = FarhangiPalette.SecondaryContainerLight,
    onSecondaryContainer = FarhangiPalette.OnSecondaryContainerLight,
    tertiary = FarhangiPalette.TertiaryLight,
    onTertiary = FarhangiPalette.OnTertiaryLight,
    tertiaryContainer = FarhangiPalette.TertiaryContainerLight,
    onTertiaryContainer = FarhangiPalette.OnTertiaryContainerLight,
    error = FarhangiPalette.ErrorLight,
    onError = FarhangiPalette.OnErrorLight,
    errorContainer = FarhangiPalette.ErrorContainerLight,
    onErrorContainer = FarhangiPalette.OnErrorContainerLight,
    background = FarhangiPalette.BackgroundLight,
    onBackground = FarhangiPalette.OnBackgroundLight,
    surface = FarhangiPalette.SurfaceLight,
    onSurface = FarhangiPalette.OnSurfaceLight,
    surfaceVariant = FarhangiPalette.SurfaceVariantLight,
    onSurfaceVariant = FarhangiPalette.OnSurfaceVariantLight,
    outline = FarhangiPalette.OutlineLight,
    outlineVariant = FarhangiPalette.OutlineVariantLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = FarhangiPalette.PrimaryDark,
    onPrimary = FarhangiPalette.OnPrimaryDark,
    primaryContainer = FarhangiPalette.PrimaryContainerDark,
    onPrimaryContainer = FarhangiPalette.OnPrimaryContainerDark,
    secondary = FarhangiPalette.SecondaryDark,
    onSecondary = FarhangiPalette.OnSecondaryDark,
    secondaryContainer = FarhangiPalette.SecondaryContainerDark,
    onSecondaryContainer = FarhangiPalette.OnSecondaryContainerDark,
    tertiary = FarhangiPalette.TertiaryDark,
    onTertiary = FarhangiPalette.OnTertiaryDark,
    tertiaryContainer = FarhangiPalette.TertiaryContainerDark,
    onTertiaryContainer = FarhangiPalette.OnTertiaryContainerDark,
    error = FarhangiPalette.ErrorDark,
    onError = FarhangiPalette.OnErrorDark,
    errorContainer = FarhangiPalette.ErrorContainerDark,
    onErrorContainer = FarhangiPalette.OnErrorContainerDark,
    background = FarhangiPalette.BackgroundDark,
    onBackground = FarhangiPalette.OnBackgroundDark,
    surface = FarhangiPalette.SurfaceDark,
    onSurface = FarhangiPalette.OnSurfaceDark,
    surfaceVariant = FarhangiPalette.SurfaceVariantDark,
    onSurfaceVariant = FarhangiPalette.OnSurfaceVariantDark,
    outline = FarhangiPalette.OutlineDark,
    outlineVariant = FarhangiPalette.OutlineVariantDark,
)

@Composable
fun FarhangiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = FarhangiTypography,
            shapes = FarhangiShapes,
            content = content,
        )
    }
}