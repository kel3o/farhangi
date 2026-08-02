package ir.farhangi.core.network.supabase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseHttpClient @Inject constructor(
    private val config: SupabaseConfig,
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = JSON_MEDIA_TYPE.toMediaType()

    suspend fun post(path: String, jsonBody: String, extraHeaders: Map<String, String> = emptyMap()): String =
        execute(
            Request.Builder()
                .url(config.url.trimEnd('/') + path)
                .post(jsonBody.toRequestBody(jsonMediaType))
                .headers(config, extraHeaders)
                .build(),
        )

    suspend fun get(path: String, extraHeaders: Map<String, String> = emptyMap()): String =
        execute(
            Request.Builder()
                .url(config.url.trimEnd('/') + path)
                .get()
                .headers(config, extraHeaders)
                .build(),
        )

    private suspend fun execute(request: Request): String = withContext(Dispatchers.IO) {
        client.newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                error("Supabase HTTP ${response.code}: $body")
            }
            body
        }
    }

    private fun Request.Builder.headers(
        config: SupabaseConfig,
        extra: Map<String, String>,
    ): Request.Builder {
        header("apikey", config.anonKey)
        header("Authorization", "Bearer ${config.anonKey}")
        header("Content-Type", JSON_MEDIA_TYPE)
        extra.forEach { (key, value) -> header(key, value) }
        return this
    }

    companion object {
        private const val TIMEOUT_SECONDS = 30L
        private const val JSON_MEDIA_TYPE = "application/json"
    }
}
