package com.reneevandervelde.system.info

import com.reneevandervelde.settings.MultitoolSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File

class SystemSettings(
) {
    val hostBinDir = File(System.getProperty("user.home"), ".local/bin")
    val hostBashProfile = File(System.getProperty("user.home"), ".bash_profile")
    val hostLibDir = File(System.getProperty("user.home"), ".local/share")
    val hostBinSystemLink = File(hostBinDir, "mt-system")

    val multitoolLibDir = File(hostLibDir, "multitool")
    val multitoolBuildDir = File(multitoolLibDir, "build")
    val multitoolGitDir = File(multitoolBuildDir, ".git")
    val systemSrcRoot = File(multitoolBuildDir, "system/src")
    val systemMainBashProfile = File(systemSrcRoot, "main/bash/profiles/main.bash_profile")
    val systemInstallBin = File(multitoolBuildDir, "system/build/install/mt-system/bin/mt-system")

    fun createDirs() {
        hostBinDir.mkdirs()
        multitoolLibDir.mkdirs()
        multitoolBuildDir.mkdirs()
    }
}

val MultitoolSettings.systemSettings: Flow<SystemSettings>
    get() = allSettings.map {
        SystemSettings()
    }
