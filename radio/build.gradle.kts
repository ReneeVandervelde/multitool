plugins {
    application
    kotlin("jvm")
}

application {
    applicationName = "mt-radio"
    mainClass.set("com.reneevandervelde.radio.MainKt")
}

dependencies {
    implementation(projects.settings)
    implementation(libs.spondee.units)
    implementation(projects.notionApi)
}
