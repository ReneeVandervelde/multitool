package com.reneevandervelde.system.commands.configure

import com.reneevandervelde.system.apps.GarbageCollector
import com.reneevandervelde.system.apps.SystemCtl

class ConfigurationModule(
    systemCtl: SystemCtl,
) {
    val configurations = listOf(
        GarbageCollector(systemCtl),
    )
}
