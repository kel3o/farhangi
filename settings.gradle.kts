pluginManagement {
    repositories {
        // Prefer official repos; Aliyun can 502 on newer plugin artifacts (e.g. KSP).
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://maven.aliyun.com/repository/google")
        maven(url = "https://maven.aliyun.com/repository/public")
        maven(url = "https://maven.aliyun.com/repository/gradle-plugin")
    }
}

dependencyResolutionManagement {
    // PREFER_SETTINGS avoids FAIL_ON conflicts with any global init.d mirrors.
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://maven.aliyun.com/repository/google")
        maven(url = "https://maven.aliyun.com/repository/public")
    }
}

rootProject.name = "Farhangi"

include(":app")

include(":core:model")
include(":core:common")
include(":core:designsystem")
include(":core:ui")
include(":core:data")
include(":core:network")
include(":core:database")
include(":core:datastore")
include(":core:navigation")

include(":feature:auth:api")
include(":feature:auth:impl")
include(":feature:home:api")
include(":feature:home:impl")
include(":feature:books:api")
include(":feature:books:impl")
include(":feature:courses:api")
include(":feature:courses:impl")
include(":feature:magazine:api")
include(":feature:magazine:impl")
include(":feature:profile:api")
include(":feature:profile:impl")
include(":feature:search:api")
include(":feature:search:impl")
include(":feature:competitions:api")
include(":feature:competitions:impl")
include(":feature:studio:api")
include(":feature:studio:impl")
