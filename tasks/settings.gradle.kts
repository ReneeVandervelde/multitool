rootProject.name = "tasks"

apply(from = "../gradle/sharedCatalogs.gradle.kts")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
    }
}

includeBuild("../notion-api")
include("android")
include("cli")
include("task-manager")
