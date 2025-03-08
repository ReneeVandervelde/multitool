package com.reneevandervelde.system.commands.update

import com.github.ajalt.mordant.terminal.Terminal
import com.reneevandervelde.settings.MultitoolSettings
import com.reneevandervelde.system.info.SystemInfoAccess
import com.reneevandervelde.system.packagemanager.PackageManager
import com.reneevandervelde.system.processes.Decision
import com.reneevandervelde.system.processes.Operation
import kimchi.logger.KimchiLogger

class UpdateModule(
    settings: MultitoolSettings,
    terminal: Terminal,
    systemInfo: SystemInfoAccess,
    packageManagers: Set<PackageManager>,
    logger: KimchiLogger,
) {
    private val selfUpdateOperation = SelfUpdateOperation(
        settings = settings,
        terminal = terminal,
        logger = logger,
    )
    val operations: List<Operation> = listOf(
        selfUpdateOperation,
        FlatpakUpdateOperation(systemInfo),
        DnfUpdateOperation(systemInfo),
        BrewUpdateOperation(systemInfo),
        SnapUpdateOperation(systemInfo),
        NpmUpdateOperation(systemInfo),
        *packageManagers.map { it.toUpdateOperation() }.toTypedArray(),
    )
}

fun PackageManager.toUpdateOperation(): Operation {
    return object: Operation {
        override val name: String = this::class.simpleName.toString()
        override suspend fun enabled(): Decision = this@toUpdateOperation.enabled()
        override suspend fun runOperation() = updateAll()
    }
}
