plugins {
    application
    kotlin("jvm")
}

application {
    applicationName = "mt-radio"
    mainClass.set("com.reneevandervelde.radio.MainKt")
}

dependencies {
    implementation(libs.bundles.cli)
    implementation(libs.spondee.units)
    implementation(projects.settings)
    implementation(projects.notionApi)
}
