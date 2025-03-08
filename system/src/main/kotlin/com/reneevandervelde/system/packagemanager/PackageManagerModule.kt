package com.reneevandervelde.system.packagemanager

import com.reneevandervelde.system.info.SystemInfoAccess

class PackageManagerModule(
    private val systemInfoAccess: SystemInfoAccess,
) {
    val packageManagers: Set<PackageManager> = setOf(
        RpmOstree(systemInfoAccess),
    )
}
