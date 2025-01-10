plugins {
    `kotlin-dsl`
}
repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
}

kotlin {
    jvmToolchain(21)
}
dependencies {
    implementation(libs.kotlin.gradle)
    implementation(libs.kotlin.serialization.gradle)
}
