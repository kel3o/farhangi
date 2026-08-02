package ir.farhangi.core.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.farhangi.core.network.BuildConfig
import ir.farhangi.core.network.demo.DemoAuthGateway
import ir.farhangi.core.network.demo.DemoContentGateway
import ir.farhangi.core.network.gateway.AuthGateway
import ir.farhangi.core.network.gateway.ContentGateway
import ir.farhangi.core.network.supabase.SupabaseAuthAdapter
import ir.farhangi.core.network.supabase.SupabaseConfig
import ir.farhangi.core.network.supabase.SupabaseContentAdapter
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideSupabaseConfig(): SupabaseConfig = SupabaseConfig(
        url = BuildConfig.SUPABASE_URL,
        anonKey = BuildConfig.SUPABASE_ANON_KEY,
        authEnabled = BuildConfig.SUPABASE_AUTH_ENABLED,
    )

    @Provides
    @Singleton
    fun provideAuthGateway(
        config: SupabaseConfig,
        demo: DemoAuthGateway,
        supabase: SupabaseAuthAdapter,
    ): AuthGateway = if (config.useSupabaseAuth) supabase else demo

    @Provides
    @Singleton
    fun provideContentGateway(
        config: SupabaseConfig,
        demo: DemoContentGateway,
        supabase: SupabaseContentAdapter,
    ): ContentGateway = if (config.useSupabaseContent) supabase else demo
}
