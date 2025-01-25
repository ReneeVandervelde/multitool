rootProject.name = "contacts"

apply(from = "../gradle/sharedCatalogs.gradle.kts")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
    }
}

includeBuild("../notion-api")
