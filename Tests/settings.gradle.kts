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

rootProject.name = "Feature Tests"

include(":auth-tests")
include(":home-tests")
include(":schedule-tests")
include(":tasks-tests")
include(":core:core_common")
include(":core:core_ui")
include(":core:core_designsystem")
include(":core:core_network")
include(":core:core_database")
include(":core:core_navigation")
include(":feature:feature_auth")
include(":feature:feature_home")
include(":feature:feature_schedule")

project(":core:core_common").projectDir = file("../Programm/core/core_common")
project(":core:core_ui").projectDir = file("../Programm/core/core_ui")
project(":core:core_designsystem").projectDir = file("../Programm/core/core_designsystem")
project(":core:core_network").projectDir = file("../Programm/core/core_network")
project(":core:core_database").projectDir = file("../Programm/core/core_database")
project(":core:core_navigation").projectDir = file("../Programm/core/core_navigation")
project(":feature:feature_auth").projectDir = file("../Programm/feature/feature_auth")
project(":feature:feature_home").projectDir = file("../Programm/feature/feature_home")
project(":feature:feature_schedule").projectDir = file("../Programm/feature/feature_schedule")
