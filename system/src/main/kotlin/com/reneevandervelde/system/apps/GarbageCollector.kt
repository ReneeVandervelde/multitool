package com.reneevandervelde.system.apps

import com.reneevandervelde.system.commands.configure.ConfigurationStatus
import com.reneevandervelde.system.commands.configure.SystemConfiguration
import com.reneevandervelde.system.processes.Decision

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

    override suspend fun enabled(): Decision
    {
        return Decision.Yes("Build-in app")
    }
}
