package com.reneevandervelde.system.apps

import com.reneevandervelde.system.apps.structures.App
import com.reneevandervelde.system.commands.configure.ConfigurationStatus
import com.reneevandervelde.system.commands.configure.SystemConfiguration
import com.reneevandervelde.system.info.OperatingSystem
import com.reneevandervelde.system.info.SystemInfoAccess
import com.reneevandervelde.system.processes.Decision
import java.io.File

class UsbGuardMonitor(
    private val systemCtl: SystemCtl,
    private val systemInfoAccess: SystemInfoAccess,
): App, SystemConfiguration {
    override val id: String = "usbguard-monitor"

    override suspend fun enabled(): Decision
    {
        val info = systemInfoAccess.getSystemInfo()

        return when {
            info.operatingSystem is OperatingSystem.Linux -> Decision.Yes("Available on Linux")
            else -> Decision.No("USBGuard not available on this OS")
        }
    }

    override suspend fun getStatus(): ConfigurationStatus
    {
        return if (systemCtl.isEnabled("usbguard-monitor.service", user = true)) {
            ConfigurationStatus.Configured
        } else {
            ConfigurationStatus.NotConfigured
        }
    }

    override suspend fun configure()
    {
        val resource = javaClass.getResourceAsStream("/usbguard-monitor.service")
        val tempFile = File.createTempFile("usbguard-monitor", ".service")
        tempFile.outputStream().use {
            resource.copyTo(it)
        }

        systemCtl.enable(tempFile, user = true)
    }
}
