package com.reneevandervelde.system.commands.update

import com.reneevandervelde.system.info.OperatingSystem
import com.reneevandervelde.system.info.SystemInfoAccess
import com.reneevandervelde.system.processes.*

class FlatpakUpdateOperation(
    private val systemInfoAccess: SystemInfoAccess,
): Operation {
    override val name: String = "Flatpak Update"

    override suspend fun enabled(): Decision
    {
        val systemInfo = systemInfoAccess.getSystemInfo()
        return when {
            systemInfo.operatingSystem is OperatingSystem.Linux -> Decision.Yes("Enabled on Linux")
            else -> Decision.No("Disabled on non-Linux systems")
        }
    }

    override suspend fun runOperation()
    {
        ShellCommand("flatpak update -y").exec(capture = true).printCapturedLines(name).awaitSuccess()
    }
}
