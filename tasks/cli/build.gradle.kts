plugins {
    application
    alias(libs.plugins.kotlin.jvm)
}

application {
    applicationName = "mt-tasks"
    mainClass.set("com.reneevandervelde.tasks.cli.MainKt")
}

dependencies {
    implementation(libs.bundles.cli)
    implementation(project(":task-manager"))
}
