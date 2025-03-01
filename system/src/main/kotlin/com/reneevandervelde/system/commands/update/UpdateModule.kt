package com.reneevandervelde.system.commands.update

import com.github.ajalt.mordant.terminal.Terminal
import com.reneevandervelde.settings.MultitoolSettings
import com.reneevandervelde.system.info.SystemInfoAccess
import com.reneevandervelde.system.processes.Operation
import kimchi.logger.KimchiLogger

class UpdateModule(
    settings: MultitoolSettings,
    terminal: Terminal,
    systemInfo: SystemInfoAccess,
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
        OstreeUpdateOperation(systemInfo),
        DnfUpdateOperation(systemInfo),
        BrewUpdateOperation(systemInfo),
    )
}
