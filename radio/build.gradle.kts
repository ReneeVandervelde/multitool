plugins {
    application
    alias(libs.plugins.kotlin.jvm)
}

application {
    applicationName = "mt-radio"
    mainClass.set("com.reneevandervelde.radio.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.cli)
    implementation(libs.spondee.units)
    implementation(libs.local.notion)
    implementation(libs.local.settings)
}
