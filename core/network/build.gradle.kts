import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

val localProperties = Properties().apply {
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        localFile.inputStream().use { load(it) }
    }
}

fun propOrEmpty(key: String): String =
    (localProperties.getProperty(key) ?: "").replace("\"", "\\\"")

fun propBool(key: String, default: Boolean = false): Boolean =
    localProperties.getProperty(key)?.toBooleanStrictOrNull() ?: default

android {
    namespace = "ir.farhangi.core.network"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("String", "SUPABASE_URL", "\"${propOrEmpty("SUPABASE_URL")}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${propOrEmpty("SUPABASE_ANON_KEY")}\"")
        // Phone SMS provider must be configured in Supabase before enabling real Auth.
        buildConfigField("boolean", "SUPABASE_AUTH_ENABLED", "${propBool("SUPABASE_AUTH_ENABLED")}")
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    api(project(":core:model"))
    api(project(":core:common"))
    api(libs.kotlinx.coroutines.android)
    api(libs.kotlinx.serialization.json)
    api(libs.kotlinx.datetime)

    implementation(libs.okhttp)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit4)
    testImplementation(libs.kotlinx.coroutines.test)
}
