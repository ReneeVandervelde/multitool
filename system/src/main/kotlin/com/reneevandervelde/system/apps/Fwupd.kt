package com.reneevandervelde.system.apps

import com.reneevandervelde.system.apps.structures.Updatable
import com.reneevandervelde.system.info.OperatingSystem
import com.reneevandervelde.system.info.SystemInfoAccess
import com.reneevandervelde.system.processes.Decision
import com.reneevandervelde.system.processes.ShellCommand
import com.reneevandervelde.system.processes.awaitSuccess
import com.reneevandervelde.system.processes.exec
import com.reneevandervelde.system.processes.printCapturedLines
import ink.ui.structures.elements.StackElement

class Fwupd(
    private val systemInfoAccess: SystemInfoAccess,
    private val output: StackElement,
): Updatable {
    override suspend fun enabled(): Decision
    {
        val systemInfo = systemInfoAccess.getSystemInfo()

        return when {
            systemInfo.operatingSystem is OperatingSystem.Linux -> Decision.Yes("Enabled on Linux systems")
            else -> Decision.No("Disabled on non-Linux systems")
        }
    }

    override suspend fun update()
    {
        ShellCommand("sudo fwupdmgr update")
            .exec(capture = true)
            .printCapturedLines(output, "fwupd")
            .awaitSuccess()
    }
}
