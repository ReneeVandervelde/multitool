package com.reneevandervelde.system.commands.update

import com.reneevandervelde.system.info.OperatingSystem.Linux.Fedora.Silverblue
import com.reneevandervelde.system.info.SystemInfoAccess
import com.reneevandervelde.system.processes.*

class OstreeUpdateOperation(
    private val systemInfoAccess: SystemInfoAccess,
): Operation {
    override val name: String = "Ostree Update"

    override suspend fun enabled(): Decision
    {
        val systemInfo = systemInfoAccess.getSystemInfo()
        return when {
            systemInfo.operatingSystem is Silverblue -> Decision.Yes("Enabled on Fedora Silverblue")
            else -> Decision.No("Disabled on non-Fedora Silverblue systems")
        }
    }

    override suspend fun runOperation()
    {
        ShellCommand("rpm-ostree upgrade").exec(capture = true).printCapturedLines(name).awaitSuccess()
    }
}
