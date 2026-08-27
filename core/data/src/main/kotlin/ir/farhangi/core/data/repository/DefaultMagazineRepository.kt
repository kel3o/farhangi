package ir.farhangi.core.data.repository

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.common.result.map
import ir.farhangi.core.data.mapper.toDomain
import ir.farhangi.core.model.Article
import ir.farhangi.core.network.gateway.ContentGateway
import ir.farhangi.core.network.gateway.EngagementGateway
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultMagazineRepository @Inject constructor(
    private val contentGateway: ContentGateway,
    private val engagementGateway: EngagementGateway,
) : MagazineRepository {
    override suspend fun getArticles(query: String?): Result<List<Article>> {
        val saved = savedIds()
        return contentGateway.getArticles(query).map { list ->
            list.map { it.toDomain(isSaved = it.id in saved) }
        }
    }

    override suspend fun getArticle(id: String): Result<Article> {
        val saved = savedIds()
        return contentGateway.getArticle(id).map { it.toDomain(isSaved = it.id in saved) }
    }

    private suspend fun savedIds(): Set<String> = when (val result = engagementGateway.getSavedArticleIds()) {
        is Result.Success -> result.data
        else -> emptySet()
    }
}
