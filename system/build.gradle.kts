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
    implementation(libs.ink.ui.render.terminal)
    implementation(libs.bundles.ktor.client)
    implementation(libs.apache.commons.compress)
    implementation(libs.ink.ui.render.terminal)
}

tasks.register("install", Exec::class) {
    dependsOn("installDist")
    workingDir = project.rootDir
    commandLine("${project.rootDir.absolutePath}/src/main/bash/install")
}
