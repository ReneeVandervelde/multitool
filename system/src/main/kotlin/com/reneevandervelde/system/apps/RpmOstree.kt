package com.reneevandervelde.system.apps

import com.reneevandervelde.system.info.OperatingSystem
import com.reneevandervelde.system.info.SystemInfoAccess
import com.reneevandervelde.system.packagemanager.PackageManager
import com.reneevandervelde.system.processes.*

class RpmOstree(
    private val systemInfoAccess: SystemInfoAccess,
): PackageManager
{
    override suspend fun enabled(): Decision
    {
        val systemInfo = systemInfoAccess.getSystemInfo()

        return when {
            systemInfo.operatingSystem is OperatingSystem.Linux.Fedora.Silverblue -> Decision.Yes("Enabled on Fedora Silverblue")
            else -> Decision.No("Disabled on non-Fedora Silverblue systems")
        }
    }

    override suspend fun updateAll()
    {
        ShellCommand("rpm-ostree upgrade")
            .exec(capture = true)
            .printCapturedLines("Ostree Update")
            .awaitSuccess()
    }
}
