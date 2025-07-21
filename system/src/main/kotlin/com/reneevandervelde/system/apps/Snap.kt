package com.reneevandervelde.system.apps

import com.reneevandervelde.system.info.OperatingSystem
import com.reneevandervelde.system.info.SystemInfoAccess
import com.reneevandervelde.system.apps.structures.PackageManager
import com.reneevandervelde.system.processes.*
import ink.ui.structures.elements.StackElement

class Snap(
    private val systemInfoAccess: SystemInfoAccess,
    private val output: StackElement,
): PackageManager {
    override suspend fun enabled(): Decision {
        val systemInfo = systemInfoAccess.getSystemInfo()

        return when {
            systemInfo.operatingSystem !is OperatingSystem.Linux -> Decision.No("Only enabled on Linux systems")
            systemInfo.missingCommand("snap") -> Decision.No("Snap not installed")
            else -> Decision.Yes("Enabled on Linux systems with Snap installed")
        }
    }

    override suspend fun update() {
        ShellCommand("sudo snap refresh")
            .exec(capture = true)
            .printCapturedLines(output, "Snap Updates")
            .awaitSuccess()
    }
}
