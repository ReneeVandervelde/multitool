package com.reneevandervelde.system.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.mordant.rendering.TextColors
import com.inkapplications.standard.throwCancels
import com.reneevandervelde.system.SystemModule
import com.reneevandervelde.system.exceptions.DocumentedResult
import com.reneevandervelde.system.exceptions.ExceptionHandleResult
import com.reneevandervelde.system.processes.ExitCode
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
    val logger by lazy { module.formattedLogger }
    abstract val errors: List<DocumentedResult>

    final override fun helpEpilog(context: Context): String {
        val allErrors = (errors + DocumentedResult.Global.values()).also {
            require(it == it.distinctBy { it.exitCode }) { "Duplicate error codes detected in command" }
        }

        context.terminal.println("test")
        val header = TextColors.yellow("Return Codes:")
        val other = allErrors
            .sortedBy { it.exitCode.code }
            .map { TextColors.magenta("${it.exitCode}") + " - ${it.meaning}" }
            .map { it.prependIndent() }

        return (listOf(header) + other).joinToString("\u0085")
    }

    final override fun run()
    {
        runBlocking {
            runCatching {
                logger.trace("Beginning command execution of ${this@SystemCommand::class.simpleName}")
                runCommand()
            }.throwCancels().onFailure { error ->
                val handleResult = module.exceptionHandler.handle(error)

                when (handleResult) {
                    is ExceptionHandleResult.Exit -> exitProcess(handleResult.code.code)
                    ExceptionHandleResult.Ignore -> Unit
                    ExceptionHandleResult.Unhandled -> {
                        logger.error("Exception thrown during command execution", error)
                        exitProcess(ExitCode.Unknown.code)
                    }
                }
            }
        }
    }

    abstract suspend fun runCommand()
}

