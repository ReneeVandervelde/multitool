package com.reneevandervelde.system.apps

import com.reneevandervelde.system.info.OperatingSystem.Linux.Fedora
import com.reneevandervelde.system.info.SystemInfoAccess
import com.reneevandervelde.system.apps.structures.PackageManager
import com.reneevandervelde.system.processes.*
import com.reneevandervelde.system.render.TtyLayout

class Dnf(
    private val systemInfoAccess: SystemInfoAccess,
    private val output: TtyLayout,
): PackageManager {
    override suspend fun enabled(): Decision
    {
        val systemInfo = systemInfoAccess.getSystemInfo()

        return when {
            systemInfo.operatingSystem !is Fedora -> Decision.No("Disabled on non-Fedora systems")
            systemInfo.operatingSystem is Fedora.Silverblue -> Decision.No("Not needed on Fedora Silverblue")
            else -> Decision.Yes("Enabled for Fedora")
        }
    }

    override suspend fun update()
    {
        ShellCommand("sudo dnf upgrade")
            .exec(capture = true)
            .printCapturedLines(output, "DNF Updates")
            .awaitSuccess()
    }
}
