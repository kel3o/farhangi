package ir.farhangi.feature.magazine.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object MagazineRoute : NavKey

@Serializable
data class ArticleDetailRoute(val articleId: String) : NavKey