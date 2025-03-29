package com.reneevandervelde.system

import com.github.ajalt.mordant.terminal.Terminal
import com.inkapplications.datetime.ZonedClock
import com.reneevandervelde.settings.SettingsModule
import com.reneevandervelde.system.commands.configure.ConfigurationModule
import com.reneevandervelde.system.exceptions.CompositeExceptionHandler
import com.reneevandervelde.system.exceptions.ExceptionHandler
import com.reneevandervelde.system.exceptions.SimpleErrorHandler
import com.reneevandervelde.system.info.SystemInfoAccess
import com.reneevandervelde.system.apps.AppsModule
import com.reneevandervelde.system.processes.OperationRunner
import com.reneevandervelde.system.render.StatusRenderer
import com.reneevandervelde.system.render.TerminalRenderer
import com.reneevandervelde.system.render.TextRenderer
import com.reneevandervelde.system.render.TtyLayout
import kimchi.Kimchi
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class SystemModule(
    terminal: Terminal,
    isVerbose: Boolean,
) {
    val ioScope = CoroutineScope(Dispatchers.IO)
    val defaultScope = CoroutineScope(Dispatchers.Default)
    val clock = ZonedClock.System
    private val logWriter = FormattedLogger.Writer(
        terminal = terminal,
        isVerbose = isVerbose,
        clock = clock,
    )
    val outputLayout = TtyLayout()
    private val logger: KimchiLogger = Kimchi.apply {
        addLog(logWriter)
    }
    val formattedLogger = FormattedLogger(
        terminal = terminal,
        logger = logger,
    )
    val settings = SettingsModule().settingsAccess
    val operationRunner = OperationRunner(
        output = outputLayout,
        runScope = ioScope,
        logger = logger,
        clock = clock,
    )
    val systemInfo = SystemInfoAccess()
    val appsModule = AppsModule(
        systemInfoAccess = systemInfo,
        multitoolSettings = settings,
        output = outputLayout,
        terminal = terminal,
        logger = logger,
    )
    val configurationModule = ConfigurationModule(
        systemCtl = appsModule.systemCtl,
        systemInfo = systemInfo,
    )
    val renderer = TerminalRenderer(
        renderers = listOf(
            TextRenderer(
                terminal = terminal,
                logger = logger,
            ),
            StatusRenderer(
                terminal = terminal,
                logger = logger,
            ),
        ),
    )
    val exceptionHandler: ExceptionHandler = CompositeExceptionHandler(
        listOf(
            SimpleErrorHandler(logger),
        )
    )
}

