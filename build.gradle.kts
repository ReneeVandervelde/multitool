tasks.register("checkAll") {
    dependsOn(gradle.includedBuilds.map { it.task(":check") })
}
tasks.register("install") {
    dependsOn(gradle.includedBuild("system").task(":install"))
}
