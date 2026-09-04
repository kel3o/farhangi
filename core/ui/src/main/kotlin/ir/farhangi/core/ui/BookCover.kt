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
import coil.compose.AsyncImage
import ir.farhangi.core.designsystem.theme.FarhangiSpacing

const val BOOK_COVER_ASPECT_RATIO = 2f / 3f

@Composable
fun BookCover(
    coverUrl: String?,
    title: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.aspectRatio(BOOK_COVER_ASPECT_RATIO),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = FarhangiSpacing.xxs,
    ) {
        CoverImageContent(
            coverUrl = coverUrl,
            title = title,
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
fun ContentImage(
    coverUrl: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val drawableId = coverDrawableId(coverUrl)
    when {
        drawableId != 0 -> Image(
            painter = painterResource(drawableId),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Fit,
        )
        isLoadableCoverUrl(coverUrl) -> AsyncImage(
            model = coverUrl,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
internal fun CoverImageContent(
    coverUrl: String?,
    title: String,
    contentScale: ContentScale,
    modifier: Modifier = Modifier,
) {
    val drawableId = coverDrawableId(coverUrl)
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when {
            drawableId != 0 -> Image(
                painter = painterResource(drawableId),
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale,
            )
            isLoadableCoverUrl(coverUrl) -> AsyncImage(
                model = coverUrl,
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale,
            )
            else -> Text(
                text = title.take(1).ifBlank { "—" },
                modifier = Modifier.padding(FarhangiSpacing.md),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

internal fun isLoadableCoverUrl(coverUrl: String?): Boolean {
    if (coverUrl.isNullOrBlank()) return false
    return coverUrl.startsWith(CONTENT_URI_PREFIX) ||
        coverUrl.startsWith(FILE_URI_PREFIX) ||
        coverUrl.startsWith(HTTP_URI_PREFIX) ||
        coverUrl.startsWith(HTTPS_URI_PREFIX)
}

private const val CONTENT_URI_PREFIX = "content:"
private const val FILE_URI_PREFIX = "file:"
private const val HTTP_URI_PREFIX = "http:"
private const val HTTPS_URI_PREFIX = "https:"


internal fun coverDrawableId(coverUrl: String?): Int = when (coverUrl) {
    "cover_golestan" -> R.drawable.cover_golestan
    "cover_kimiagar" -> R.drawable.cover_kimiagar
    "cover_tarikh_tamadon" -> R.drawable.cover_tarikh_tamadon
    "cover_adab_moasherat" -> R.drawable.cover_adab_moasherat
    "cover_khoshnevisi" -> R.drawable.cover_khoshnevisi
    "cover_eghtesad" -> R.drawable.cover_eghtesad
    "cover_ghese_madarbozorg" -> R.drawable.cover_ghese_madarbozorg
    "cover_maharat_goftogo" -> R.drawable.cover_maharat_goftogo
    "cover_hafez" -> R.drawable.cover_hafez
    "cover_shahnameh" -> R.drawable.cover_shahnameh
    "cover_boostan" -> R.drawable.cover_boostan
    "cover_masnavi" -> R.drawable.cover_masnavi
    "cover_little_prince" -> R.drawable.cover_little_prince
    "cover_animal_farm" -> R.drawable.cover_animal_farm
    "cover_psychology" -> R.drawable.cover_psychology
    "cover_parenting" -> R.drawable.cover_parenting
    "cover_marg_tajerane" -> R.drawable.cover_marg_tajerane
    "cover_course_empathy" -> R.drawable.cover_course_empathy
    "cover_course_geometry" -> R.drawable.cover_course_geometry
    "cover_course_network" -> R.drawable.cover_course_network
    "cover_course_world" -> R.drawable.cover_course_world
    "cover_course_play" -> R.drawable.cover_course_play
    "cover_course_music" -> R.drawable.cover_course_music
    "cover_course_dialogue" -> R.drawable.cover_course_dialogue
    "cover_course_family" -> R.drawable.cover_course_family
    "cover_course_calligraphy" -> R.drawable.cover_course_calligraphy
    "cover_article_1" -> R.drawable.cover_article_1
    "cover_article_2" -> R.drawable.cover_article_2
    "cover_article_3" -> R.drawable.cover_article_3
    "cover_article_4" -> R.drawable.cover_article_4
    "cover_article_5" -> R.drawable.cover_article_5
    "cover_article_6" -> R.drawable.cover_article_6
    "cover_article_7" -> R.drawable.cover_article_7
    "cover_article_8" -> R.drawable.cover_article_8
    "cover_article_9" -> R.drawable.cover_article_9
    else -> 0
}
