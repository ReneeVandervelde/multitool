package com.reneevandervelde.system.apps

import com.github.ajalt.mordant.terminal.Terminal
import com.reneevandervelde.settings.MultitoolSettings
import com.reneevandervelde.system.info.SystemInfoAccess
import com.reneevandervelde.system.apps.structures.PackageManager
import com.reneevandervelde.system.apps.structures.Updatable
import com.reneevandervelde.system.render.TtyLayout
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kimchi.logger.KimchiLogger

class AppsModule(
    systemInfoAccess: SystemInfoAccess,
    multitoolSettings: MultitoolSettings,
    output: TtyLayout,
    terminal: Terminal,
    logger: KimchiLogger,
) {
    val multitoolSelf = MultitoolSelf(
        settings = multitoolSettings,
        output =  output,
        terminal = terminal,
        logger = logger,
    )
    val systemCtl = SystemCtl(
        systemInfoAccess = systemInfoAccess,
        logger = logger,
    )
    val resilioSync = ResilioSync(
        settings = multitoolSettings,
        logger = logger,
        httpClient = HttpClient(CIO),
    )

    val packageManagers: Set<PackageManager> = setOf(
        Dnf(systemInfoAccess, output),
        Flatpak(systemInfoAccess, output),
        Homebrew(systemInfoAccess, output),
        Npm(systemInfoAccess, output),
        RpmOstree(systemInfoAccess, output),
        Snap(systemInfoAccess, output),
    )

    val updatables: Set<Updatable> = setOf(
        multitoolSelf,
        *packageManagers.toTypedArray(),
        resilioSync,
    )
}
