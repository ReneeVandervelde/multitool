package com.reneevandervelde.system.apps

import com.reneevandervelde.system.info.SystemInfoAccess
import com.reneevandervelde.system.apps.structures.PackageManager
import com.reneevandervelde.system.processes.*
import ink.ui.structures.elements.StackElement

class Npm(
    private val systemInfoAccess: SystemInfoAccess,
    private val output: StackElement,
): PackageManager {
    override suspend fun enabled(): Decision
    {
        val systemInfo = systemInfoAccess.getSystemInfo()

        return when {
            systemInfo.missingCommand("npm") -> Decision.No("Npm not installed")
            else -> Decision.Yes("Enabled on systems with Npm installed")
        }
    }

    override suspend fun update() {
        val name = "Npm Updates"

        ShellCommand("npm install npm -g")
            .exec(capture = true)
            .printCapturedLines(output, name)
            .awaitSuccess()

        ShellCommand("npm update -g")
            .exec(capture = true)
            .printCapturedLines(output, name)
            .awaitSuccess()
    }
}
