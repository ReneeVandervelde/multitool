package com.reneevandervelde.system.apps

import com.reneevandervelde.system.info.SystemInfoAccess
import com.reneevandervelde.system.processes.*
import kimchi.logger.KimchiLogger

class SystemCtl(
    private val systemInfoAccess: SystemInfoAccess,
    private val logger: KimchiLogger,
): App {
    override suspend fun enabled(): Decision
    {
        val systemInfo = systemInfoAccess.getSystemInfo()

        return when {
            systemInfo.missingCommand("systemctl") -> Decision.No("Systemctl not installed")
            else -> Decision.Yes("Enabled on systems with Systemctl installed")
        }
    }

    suspend fun isEnabled(
        service: String,
        user: Boolean = false,
    ): Boolean {
        val args = listOfNotNull(
            "systemctl",
            "--user".takeIf { user },
            "is-enabled",
            service,
        )
        val result = ShellCommand(*args.toTypedArray())
            .exec(capture = true)
            .logCapturedLines(logger)
            .awaitCompletion()

        return result is ProcessState.Success
    }
}
