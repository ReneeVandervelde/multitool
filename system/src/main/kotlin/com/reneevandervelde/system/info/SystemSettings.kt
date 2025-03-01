package com.reneevandervelde.system.info

import com.reneevandervelde.settings.MultitoolSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File

data class SystemSettings(
    val systemDir: File,
    val buildDir: File,
) {
    val buildGitDir = File(buildDir, ".git")

    fun createDirs() {
        systemDir.mkdirs()
        buildDir.mkdirs()
    }
}

val MultitoolSettings.systemSettings: Flow<SystemSettings>
    get() = allSettings.map {
        val systemDir = it["system-dir"]?.let(::File) ?: File(System.getProperty("user.home"), ".local/share/multitool")
        SystemSettings(
            systemDir = systemDir,
            buildDir = it["system-dir-build"]?.let(::File) ?: File(systemDir, "build")
        )
    }
