package com.reneevandervelde.system.apps

import com.reneevandervelde.system.info.SystemInfoAccess
import com.reneevandervelde.system.packagemanager.PackageManager

class AppsModule(
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
