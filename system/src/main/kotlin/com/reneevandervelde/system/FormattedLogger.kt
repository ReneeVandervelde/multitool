package com.reneevandervelde.system

import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.rendering.TextStyles.*
import com.github.ajalt.mordant.terminal.Terminal
import com.inkapplications.datetime.ZonedClock
import kimchi.logger.LogLevel
import kimchi.logger.LogWriter

class FormattedLogger(
    private val terminal: Terminal,
    private val isVerbose: Boolean,
    private val clock: ZonedClock,
): LogWriter {
    override fun shouldLog(level: LogLevel, cause: Throwable?): Boolean
    {
        return isVerbose || level >= LogLevel.INFO
    }

    override fun log(level: LogLevel, message: String, cause: Throwable?)
    {
        val time = if (isVerbose) {
            clock.localDateTime().let {
                "[$it] "
            }
        } else ""
        val formattedMessage = when (level) {
            LogLevel.TRACE -> "trace: $message"
            LogLevel.DEBUG -> "debug: $message"
            LogLevel.INFO -> magenta("> $message")
            LogLevel.WARNING -> bold(yellow(">> $message"))
            LogLevel.ERROR -> bold(red(">>> $message"))
        }

        terminal.println("$time$formattedMessage")
    }
}
