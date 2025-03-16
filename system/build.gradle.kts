plugins {
    application
    alias(libs.plugins.kotlin.jvm)
}

application {
    applicationName = "mt-system"
    mainClass.set("com.reneevandervelde.system.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.cli)
    implementation(libs.bundles.kotlin.extensions)
    implementation(libs.local.settings)
    implementation(libs.ink.ui.structures)
}

tasks.register("install", Exec::class) {
    workingDir = project.rootDir
    commandLine("${project.rootDir.absolutePath}/src/main/bash/install-profile")
}
