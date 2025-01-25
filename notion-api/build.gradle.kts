plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

apply(from = "../gradle/library.gradle.kts")

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.bundles.ktor.client)
            api(libs.kotlin.datetime)
        }
    }
}
