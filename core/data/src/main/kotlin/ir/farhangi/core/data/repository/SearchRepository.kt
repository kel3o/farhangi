package ir.farhangi.core.data.repository

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.model.SearchContentType
import ir.farhangi.core.model.SearchResult

interface SearchRepository {
    suspend fun search(query: String, type: SearchContentType? = null): Result<List<SearchResult>>
}