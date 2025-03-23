tasks.register("check") {
    // invoke all subprojects' check tasks
    dependsOn(subprojects.map { it.tasks.named("check") })
}

repositories {
    mavenCentral()
}

subprojects {
    repositories {
        mavenCentral()
    }
}
