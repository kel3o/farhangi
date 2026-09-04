package ir.farhangi.core.designsystem.theme

import androidx.compose.ui.graphics.Color

/**
 * Seed-based Material 3 palette fallback (when Dynamic Color unavailable).
 * Cultural, calm, professional — teal-green primary.
 */
internal object FarhangiPalette {
    val Seed = Color(0xFF0F6E56)

    val PrimaryLight = Color(0xFF0F6E56)
    val OnPrimaryLight = Color(0xFFFFFFFF)
    val PrimaryContainerLight = Color(0xFFA2F2D3)
    val OnPrimaryContainerLight = Color(0xFF002117)

    val SecondaryLight = Color(0xFF4C635A)
    val OnSecondaryLight = Color(0xFFFFFFFF)
    val SecondaryContainerLight = Color(0xFFCEE9DC)
    val OnSecondaryContainerLight = Color(0xFF082018)

    val TertiaryLight = Color(0xFF3F6074)
    val OnTertiaryLight = Color(0xFFFFFFFF)
    val TertiaryContainerLight = Color(0xFFC2E8FF)
    val OnTertiaryContainerLight = Color(0xFF001F2B)

    val ErrorLight = Color(0xFFBA1A1A)
    val OnErrorLight = Color(0xFFFFFFFF)
    val ErrorContainerLight = Color(0xFFFFDAD6)
    val OnErrorContainerLight = Color(0xFF410002)

    val BackgroundLight = Color(0xFFF5FBF6)
    val OnBackgroundLight = Color(0xFF171D1A)
    val SurfaceLight = Color(0xFFF5FBF6)
    val OnSurfaceLight = Color(0xFF171D1A)
    val SurfaceVariantLight = Color(0xFFDBE5DF)
    val OnSurfaceVariantLight = Color(0xFF3F4944)
    val OutlineLight = Color(0xFF6F7974)
    val OutlineVariantLight = Color(0xFFBFC9C3)

    val PrimaryDark = Color(0xFF86D6B8)
    val OnPrimaryDark = Color(0xFF003829)
    val PrimaryContainerDark = Color(0xFF00513D)
    val OnPrimaryContainerDark = Color(0xFFA2F2D3)

    val SecondaryDark = Color(0xFFB3CCC0)
    val OnSecondaryDark = Color(0xFF1E352C)
    val SecondaryContainerDark = Color(0xFF354B42)
    val OnSecondaryContainerDark = Color(0xFFCEE9DC)

    val TertiaryDark = Color(0xFFA7CCE0)
    val OnTertiaryDark = Color(0xFF093544)
    val TertiaryContainerDark = Color(0xFF264B5C)
    val OnTertiaryContainerDark = Color(0xFFC2E8FF)

    val ErrorDark = Color(0xFFFFB4AB)
    val OnErrorDark = Color(0xFF690005)
    val ErrorContainerDark = Color(0xFF93000A)
    val OnErrorContainerDark = Color(0xFFFFDAD6)

    val BackgroundDark = Color(0xFF0F1512)
    val OnBackgroundDark = Color(0xFFDEE4DF)
    val SurfaceDark = Color(0xFF0F1512)
    val OnSurfaceDark = Color(0xFFDEE4DF)
    val SurfaceVariantDark = Color(0xFF3F4944)
    val OnSurfaceVariantDark = Color(0xFFBFC9C3)
    val OutlineDark = Color(0xFF89938E)
    val OutlineVariantDark = Color(0xFF3F4944)
}

/** Explicit purchase action — distinct from the teal primary seed. */
object FarhangiActionColors {
    val Purchase = Color(0xFF2E7D32)
    val OnPurchase = Color(0xFFFFFFFF)
}

/** Representational medal metals — not Material color roles. */
object FarhangiHonorColors {
    val Gold = Color(0xFFC9A227)
    val GoldDeep = Color(0xFF7A5C00)
    val Silver = Color(0xFF8A9199)
    val SilverDeep = Color(0xFF4A5056)
    val Bronze = Color(0xFFB87333)
    val BronzeDeep = Color(0xFF6B3F1A)
    val OnMedal = Color(0xFFFFFFFF)
    val WeeklyRibbon = Color(0xFF1565C0)
    val MonthlyRibbon = Color(0xFFC62828)
    val Wreath = Color(0xFF3D4A3D)
}

/** Semantic contest status fills — pale green / pale red as specified. */
object FarhangiContestColors {
    val LiveContainer = Color(0xFFC8E6C9)
    val OnLive = Color(0xFF1B5E20)
    val FinishedContainer = Color(0xFFFFCDD2)
    val OnFinished = Color(0xFFB71C1C)
}