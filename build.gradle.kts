tasks.register("checkAll") {
    dependsOn(gradle.includedBuilds.map { it.task(":check") })
}
