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
import ink.ui.render.terminal.TerminalPresenter
import ink.ui.structures.elements.StackElement
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
    val outputElement = StackElement()
    private val logger: KimchiLogger = Kimchi.apply {
        addLog(logWriter)
    }
    val formattedLogger = FormattedLogger(
        terminal = terminal,
        logger = logger,
    )
    val settings = SettingsModule().settingsAccess
    val operationRunner = OperationRunner(
        output = outputElement,
        runScope = ioScope,
        logger = logger,
        clock = clock,
    )
    val systemInfo = SystemInfoAccess()
    val appsModule = AppsModule(
        systemInfoAccess = systemInfo,
        multitoolSettings = settings,
        output = outputElement,
        terminal = terminal,
        logger = logger,
    )
    val configurationModule = ConfigurationModule(
        systemCtl = appsModule.systemCtl,
        systemInfo = systemInfo,
        settings = settings,
        clock = clock,
        logger = logger,
    )
    val renderer = TerminalPresenter()
    val exceptionHandler: ExceptionHandler = CompositeExceptionHandler(
        listOf(
            SimpleErrorHandler(logger),
        )
    )
}

