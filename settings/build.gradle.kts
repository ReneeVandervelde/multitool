plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

apply(from = "../gradle/library.gradle.kts")

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlin.coroutines.core)
        }
    }
}
