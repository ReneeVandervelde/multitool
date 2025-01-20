rootProject.name = "radio"

apply(from = "../gradle/sharedCatalogs.gradle.kts")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
    }
}

includeBuild("../notion-api")
