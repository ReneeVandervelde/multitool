plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlin.coroutines.core)
            api(libs.ink.ui.structures)
            api(libs.local.notion)
            implementation(libs.regolith.data)
        }
    }
}
