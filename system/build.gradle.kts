import java.nio.file.Files

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
}

tasks.register("configureSelf", Exec::class) {
    dependsOn("installDist")
    commandLine("${project.rootDir.absolutePath}/build/install/mt-system/bin/mt-system", "configure", "self")
}

tasks.register("installProfile", Exec::class) {
    workingDir = project.rootDir
    commandLine("${project.rootDir.absolutePath}/src/main/bash/install-profile")
}
tasks.register("installAndConfigure") {
    dependsOn("installProfile")
    dependsOn("configureSelf")
}
