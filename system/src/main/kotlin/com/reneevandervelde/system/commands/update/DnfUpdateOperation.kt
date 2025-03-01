package com.reneevandervelde.system.commands.update

import com.reneevandervelde.system.info.OperatingSystem.Linux.Fedora
import com.reneevandervelde.system.info.SystemInfoAccess
import com.reneevandervelde.system.processes.*

class DnfUpdateOperation(
    private val systemInfoAccess: SystemInfoAccess,
): Operation {
    override val name: String = "Dnf Update"

    override suspend fun enabled(): Decision
    {
        val systemInfo = systemInfoAccess.getSystemInfo()
        return when {
            systemInfo.operatingSystem !is Fedora -> Decision.No("Disabled on non-Fedora systems")
            systemInfo.operatingSystem is Fedora.Silverblue -> Decision.No("Not needed on Fedora Silverblue")
            else -> Decision.Yes("Enabled for Fedora")
        }
    }

    override suspend fun runOperation()
    {
        ShellCommand("sudo dnf upgrade").exec(capture = true).printCapturedLines(name).awaitSuccess()
    }
}
