package com.reneevandervelde.system.commands.update

import com.reneevandervelde.system.info.OperatingSystem
import com.reneevandervelde.system.info.SystemInfoAccess
import com.reneevandervelde.system.processes.*

class SnapUpdateOperation(
    private val systemInfoAccess: SystemInfoAccess,
): Operation {
    override val name: String = "Snap Update"

    override suspend fun enabled(): Decision
    {
        val systemInfo = systemInfoAccess.getSystemInfo()
        return when {
            systemInfo.operatingSystem !is OperatingSystem.Linux -> Decision.No("Only enabled on Linux systems")
            systemInfo.missingCommand("snap") -> Decision.No("Snap not installed")
            else -> Decision.Yes("Enabled on Linux systems with Snap installed")
        }
    }

    override suspend fun runOperation()
    {
        ShellCommand("sudo snap refresh").exec(capture = true).printCapturedLines(name).awaitSuccess()
    }
}
