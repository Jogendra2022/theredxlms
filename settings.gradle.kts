pluginManagement {
    repositories {
        google {
            content {includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

// THIS BLOCK HAS BEEN CORRECTED
plugins {
    // Use the ID and version from your libs.versions.toml
    id("com.android.application") version "8.13.0" apply false

    // This line is correct
    id("com.google.gms.google-services") version "4.4.2" apply false
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "The Redx LMS"
include(":app")
