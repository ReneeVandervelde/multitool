package com.reneevandervelde.system.commands.update

import com.reneevandervelde.system.info.SystemInfoAccess
import com.reneevandervelde.system.processes.*

class NpmUpdateOperation(
    private val systemInfoAccess: SystemInfoAccess,
): Operation {
    override val name: String = "Npm Update"

    override suspend fun enabled(): Decision
    {
        val systemInfo = systemInfoAccess.getSystemInfo()
        return when {
            systemInfo.missingCommand("npm") -> Decision.No("Npm not installed")
            else -> Decision.Yes("Enabled on systems with Npm installed")
        }
    }

    override suspend fun runOperation()
    {
        ShellCommand("npm install npm -g").exec(capture = true).printCapturedLines(name).awaitSuccess()
        ShellCommand("npm update -g").exec(capture = true).printCapturedLines(name).awaitSuccess()
    }
}
