package com.reneevandervelde.system.apps

import com.github.ajalt.mordant.terminal.Terminal
import com.reneevandervelde.settings.MultitoolSettings
import com.reneevandervelde.system.info.SystemInfoAccess
import com.reneevandervelde.system.apps.packagemanager.PackageManager
import kimchi.logger.KimchiLogger

class AppsModule(
    systemInfoAccess: SystemInfoAccess,
    multitoolSettings: MultitoolSettings,
    terminal: Terminal,
    logger: KimchiLogger,
) {
    val multitoolSelf = MultitoolSelf(
        settings = multitoolSettings,
        terminal = terminal,
        logger = logger,
    )

    val packageManagers: Set<PackageManager> = setOf(
        Dnf(systemInfoAccess),
        Flatpak(systemInfoAccess),
        Homebrew(systemInfoAccess),
        Npm(systemInfoAccess),
        RpmOstree(systemInfoAccess),
        Snap(systemInfoAccess),
    )

    val updatables: Set<Updatable> = setOf(
        multitoolSelf,
        *packageManagers.toTypedArray(),
    )
}
