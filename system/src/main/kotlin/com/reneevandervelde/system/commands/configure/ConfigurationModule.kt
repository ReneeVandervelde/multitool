package com.reneevandervelde.system.commands.configure

import com.reneevandervelde.settings.MultitoolSettings
import com.reneevandervelde.system.apps.*
import com.reneevandervelde.system.info.SystemInfoAccess
import kimchi.logger.KimchiLogger
import kotlinx.datetime.Clock

class ConfigurationModule(
    systemCtl: SystemCtl,
    systemInfo: SystemInfoAccess,
    settings: MultitoolSettings,
    clock: Clock,
    logger: KimchiLogger,
) {
    val configurations: List<SystemConfiguration> = listOf(
        GarbageCollector(systemCtl),
        UsbGuardMonitor(systemCtl, systemInfo),
        GitConfig(settings, clock, logger),
        GitIgnore(settings, clock, logger),
    )
}
