package com.reneevandervelde.system.apps

import com.reneevandervelde.system.info.SystemInfoAccess
import com.reneevandervelde.system.processes.*
import kimchi.logger.KimchiLogger
import java.io.File

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

    suspend fun enable(
        resource: File,
        user: Boolean = false,
    ) {
        if (!resource.exists()) {
            throw IllegalArgumentException("Resource does not exist: ${resource.absolutePath}")
        }
        val userServiceDir = File(System.getProperty("user.home"), ".config/systemd/user")
        val systemServiceDir = File("/etc/systemd/user")
        val serviceDirectory = if (user) userServiceDir else systemServiceDir
        serviceDirectory.mkdirs()
        val destination = File(serviceDirectory, "garbage-collector.service")
        logger.debug("Copying config resource ${resource.absolutePath} to  ${destination.absolutePath}")
        resource.copyTo(destination, overwrite = true)

        val args = listOfNotNull(
            "systemctl",
            "--user".takeIf { user },
            "enable",
            destination.name,
        )
        ShellCommand(command = args)
            .exec(capture = true)
            .logCapturedLines(logger)
            .awaitSuccess()
    }
}
