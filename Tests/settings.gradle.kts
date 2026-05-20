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
    versionCatalogs {
        create("libs") {
            from(files("../Programm/gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "Auth Tests"

include(":auth-tests")
include(":core:core_common")
include(":core:core_ui")
include(":core:core_designsystem")
include(":core:core_network")
include(":core:core_navigation")
include(":feature:feature_auth")

project(":core:core_common").projectDir = file("../Programm/core/core_common")
project(":core:core_ui").projectDir = file("../Programm/core/core_ui")
project(":core:core_designsystem").projectDir = file("../Programm/core/core_designsystem")
project(":core:core_network").projectDir = file("../Programm/core/core_network")
project(":core:core_navigation").projectDir = file("../Programm/core/core_navigation")
project(":feature:feature_auth").projectDir = file("../Programm/feature/feature_auth")
