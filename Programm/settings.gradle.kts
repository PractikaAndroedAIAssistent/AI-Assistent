pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "Assistent AI"

include(":app")
include(":core:core_common")
include(":core:core_designsystem")
include(":core:core_ui")
include(":core:core_network")
include(":core:core_database")
include(":core:core_navigation")
include(":feature:feature_auth")
include(":feature:feature_home")
include(":feature:feature_schedule")
include(":feature:feature_tasks")
include(":feature:feature_grades")
include(":feature:feature_notes")
