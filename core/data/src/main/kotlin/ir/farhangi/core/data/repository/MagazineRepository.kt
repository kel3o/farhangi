package ir.farhangi.core.data.repository

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.model.Article

interface MagazineRepository {
    suspend fun getArticles(query: String? = null): Result<List<Article>>
    suspend fun getArticle(id: String): Result<Article>
}