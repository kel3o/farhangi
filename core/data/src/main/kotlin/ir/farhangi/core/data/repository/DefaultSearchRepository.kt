package ir.farhangi.core.data.repository

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.common.result.map
import ir.farhangi.core.data.mapper.toDomain
import ir.farhangi.core.model.SearchContentType
import ir.farhangi.core.model.SearchResult
import ir.farhangi.core.network.gateway.ContentGateway
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultSearchRepository @Inject constructor(
    private val contentGateway: ContentGateway,
) : SearchRepository {
    override suspend fun search(query: String, type: SearchContentType?): Result<List<SearchResult>> =
        contentGateway.search(query, type?.name).map { list -> list.map { it.toDomain() } }
}