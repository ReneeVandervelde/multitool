package com.reneevandervelde.system.apps

import com.reneevandervelde.system.commands.configure.ConfigurationStatus
import com.reneevandervelde.system.commands.configure.SystemConfiguration
import com.reneevandervelde.system.processes.Decision
import java.io.File

class GarbageCollector(
    private val systemCtl: SystemCtl,
): App, SystemConfiguration {
    override val id = "garbage-collector"

    override suspend fun getStatus(): ConfigurationStatus
    {
        return if (systemCtl.isEnabled("garbage-collector.service", user = true)) {
            ConfigurationStatus.Configured
        } else {
            ConfigurationStatus.NotConfigured
        }
    }

    override suspend fun configure()
    {
        val resource = javaClass.getResourceAsStream("/garbage-collector.service")
        val tempFile = File.createTempFile("garbage-collector", ".service")
        tempFile.outputStream().use {
            resource.copyTo(it)
        }

        systemCtl.enable(tempFile, user = true)
    }

    override suspend fun enabled(): Decision
    {
        return Decision.Yes("Built-in app")
    }
}
