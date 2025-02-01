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
    implementation(libs.local.settings)
}
