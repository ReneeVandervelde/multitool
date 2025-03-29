package com.reneevandervelde.system.commands.configure

import com.reneevandervelde.system.apps.GarbageCollector
import com.reneevandervelde.system.apps.SystemCtl
import com.reneevandervelde.system.apps.UsbGuardMonitor
import com.reneevandervelde.system.info.SystemInfoAccess

class ConfigurationModule(
    systemCtl: SystemCtl,
    systemInfo: SystemInfoAccess,
) {
    val configurations: List<SystemConfiguration> = listOf(
        GarbageCollector(systemCtl),
        UsbGuardMonitor(systemCtl, systemInfo),
    )
}
