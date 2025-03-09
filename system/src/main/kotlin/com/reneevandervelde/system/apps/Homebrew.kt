package com.reneevandervelde.system.apps

import com.reneevandervelde.system.info.OperatingSystem
import com.reneevandervelde.system.info.SystemInfoAccess
import com.reneevandervelde.system.apps.packagemanager.PackageManager
import com.reneevandervelde.system.processes.*

class Homebrew(
    val systemInfoAccess: SystemInfoAccess,
): PackageManager {
    override suspend fun enabled(): Decision
    {
        val systemInfo = systemInfoAccess.getSystemInfo()

        return when {
            systemInfo.operatingSystem !is OperatingSystem.MacOS -> Decision.No("Only enabled on MacOS systems")
            systemInfo.missingCommand("brew") -> Decision.No("Brew not installed")
            else -> Decision.Yes("Enabled on MacOS systems with Brew installed")
        }
    }

    override suspend fun update()
    {
        val name = "Homebrew Updates"

        ShellCommand("brew update")
            .exec(capture = true)
            .printCapturedLines(name)
            .awaitSuccess()

        ShellCommand("brew upgrade")
            .exec(capture = true)
            .printCapturedLines(name)
            .awaitSuccess()

        ShellCommand("brew cleanup")
            .exec(capture = true)
            .printCapturedLines(name)
            .awaitSuccess()

    }
}
