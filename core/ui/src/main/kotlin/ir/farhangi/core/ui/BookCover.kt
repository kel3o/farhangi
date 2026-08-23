package ir.farhangi.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import ir.farhangi.core.designsystem.theme.FarhangiSpacing

const val BOOK_COVER_ASPECT_RATIO = 2f / 3f

@Composable
fun BookCover(
    coverUrl: String?,
    title: String,
    modifier: Modifier = Modifier,
) {
    val drawableId = coverDrawableId(coverUrl)
    Surface(
        modifier = modifier.aspectRatio(BOOK_COVER_ASPECT_RATIO),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = FarhangiSpacing.xxs,
    ) {
        if (drawableId != 0) {
            Image(
                painter = painterResource(drawableId),
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = title.take(1),
                    modifier = Modifier.padding(FarhangiSpacing.md),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

internal fun coverDrawableId(coverUrl: String?): Int = when (coverUrl) {
    "cover_golestan" -> R.drawable.cover_golestan
    "cover_kimiagar" -> R.drawable.cover_kimiagar
    "cover_tarikh_tamadon" -> R.drawable.cover_tarikh_tamadon
    "cover_adab_moasherat" -> R.drawable.cover_adab_moasherat
    "cover_khoshnevisi" -> R.drawable.cover_khoshnevisi
    "cover_eghtesad" -> R.drawable.cover_eghtesad
    "cover_ghese_madarbozorg" -> R.drawable.cover_ghese_madarbozorg
    "cover_maharat_goftogo" -> R.drawable.cover_maharat_goftogo
    else -> 0
}
