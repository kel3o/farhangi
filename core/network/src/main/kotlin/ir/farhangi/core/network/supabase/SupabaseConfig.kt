package ir.farhangi.core.network.supabase

/**
 * Runtime Supabase configuration sourced from BuildConfig / local.properties.
 * Never commit real secrets — leave blank to fall back to Demo gateways.
 *
 * [authEnabled] stays false until a phone SMS provider is configured in Supabase.
 */
data class SupabaseConfig(
    val url: String,
    val anonKey: String,
    val authEnabled: Boolean = false,
) {
    val isConfigured: Boolean
        get() = url.isNotBlank() && anonKey.isNotBlank()

    val useSupabaseAuth: Boolean
        get() = isConfigured && authEnabled

    val useSupabaseContent: Boolean
        get() = isConfigured
}
