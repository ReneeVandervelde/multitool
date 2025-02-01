package com.reneevandervelde.system

import com.github.ajalt.mordant.terminal.Terminal
import com.inkapplications.datetime.ZonedClock
import com.reneevandervelde.settings.SettingsModule
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
    private val logWriter = FormattedLogger(terminal, isVerbose, clock)
    val logger: KimchiLogger = Kimchi.apply {
        addLog(logWriter)
    }
    val settings = SettingsModule().settingsAccess
}

