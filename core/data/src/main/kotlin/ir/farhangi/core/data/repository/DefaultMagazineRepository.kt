package ir.farhangi.core.data.repository

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.common.result.map
import ir.farhangi.core.data.mapper.toDomain
import ir.farhangi.core.model.Article
import ir.farhangi.core.network.gateway.ContentGateway
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultMagazineRepository @Inject constructor(
    private val contentGateway: ContentGateway,
) : MagazineRepository {
    override suspend fun getArticles(query: String?): Result<List<Article>> =
        contentGateway.getArticles(query).map { list -> list.map { it.toDomain() } }

    override suspend fun getArticle(id: String): Result<Article> =
        contentGateway.getArticle(id).map { it.toDomain() }
}