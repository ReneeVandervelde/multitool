import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.compose)
}

tasks.configureEach {
    if (name.startsWith("lint")) {
        enabled = false
    }
}

repositories {
    mavenCentral()
    google()
}

android {
    namespace = "com.reneevandervelde.contacts"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.inkapplications.glassconsole"
        minSdk = 33
        targetSdk = 35
        versionCode = project.properties.getOrDefault("versionCode", "1").toString().toInt()
        versionName = project.properties.getOrDefault("versionName", "SNAPSHOT").toString()
    }
    buildFeatures {
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JvmTarget.JVM_17.target
    }
}

dependencies {
    implementation(libs.bundles.android)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.ink.ui.render.compose)
    implementation(libs.local.notion)
}
