package com.reneevandervelde.system.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.inkapplications.standard.throwCancels
import com.reneevandervelde.system.SystemModule
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

abstract class SystemCommand: CliktCommand()
{
    val verbose by option("-v", "--verbose", help = "Enable verbose output").flag()
    val module by lazy {
        SystemModule(
            terminal = terminal,
            isVerbose = verbose,
        )
    }
    val logger by lazy { module.logger }

    final override fun run()
    {
        runBlocking {
            runCatching {
                logger.trace("Beginning command execution of ${this@SystemCommand::class.simpleName}")
                runCommand()
            }.throwCancels().onFailure { error ->
                module.logger.error("Exception thrown during command execution", error)
                exitProcess(1)
            }
        }
    }

    abstract suspend fun runCommand()
}
