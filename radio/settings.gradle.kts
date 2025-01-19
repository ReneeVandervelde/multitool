rootProject.name = "radio"

apply(from = "../gradle/sharedCatalogs.gradle.kts")

includeBuild("../notion-api")
includeBuild("../settings")
