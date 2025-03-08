package com.reneevandervelde.system.packagemanager

import com.reneevandervelde.system.info.SystemInfoAccess

class PackageManagerModule(
    private val systemInfoAccess: SystemInfoAccess,
) {
    val packageManagers: Set<PackageManager> = setOf(
        Dnf(systemInfoAccess),
        Flatpak(systemInfoAccess),
        Homebrew(systemInfoAccess),
        Npm(systemInfoAccess),
        RpmOstree(systemInfoAccess),
        Snap(systemInfoAccess),
    )
}
