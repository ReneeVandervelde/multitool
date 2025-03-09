package com.reneevandervelde.system.apps

import com.reneevandervelde.system.info.OperatingSystem.Linux
import com.reneevandervelde.system.info.SystemInfoAccess
import com.reneevandervelde.system.apps.packagemanager.PackageManager
import com.reneevandervelde.system.processes.*

class Flatpak(
    private val systemInfoAccess: SystemInfoAccess,
): PackageManager {
    override suspend fun enabled(): Decision
    {
        val systemInfo = systemInfoAccess.getSystemInfo()

        return when {
            systemInfo.operatingSystem !is Linux -> Decision.No("Only enabled on Linux systems")
            systemInfo.missingCommand("flatpak") -> Decision.No("Flatpak not installed")
            else -> Decision.Yes("Enabled on Linux systems with Flatpak installed")
        }
    }

    override suspend fun update()
    {
        ShellCommand("flatpak update -y")
            .exec(capture = true)
            .printCapturedLines("Flatpak Updates")
            .awaitSuccess()
    }
}
