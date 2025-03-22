package com.reneevandervelde.system.commands.configure

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.types.enum
import com.reneevandervelde.system.commands.SystemCommand
import com.reneevandervelde.system.commands.configure.ConfigurationStatus.Configured
import com.reneevandervelde.system.commands.configure.ConfigurationStatus.Error
import com.reneevandervelde.system.commands.configure.ConfigurationStatus.NotConfigured
import com.reneevandervelde.system.exceptions.DocumentedResult
import com.reneevandervelde.system.processes.ExitCode
import com.reneevandervelde.system.render.println
import ink.ui.structures.Sentiment
import ink.ui.structures.TextStyle
import ink.ui.structures.elements.StatusIndicatorElement

object ConfigurationCommand: SystemCommand()
{
    private val invalidConfigError = DocumentedResult(
        exitCode = ExitCode(12),
        meaning = "The specified configuration was not found",
    )
    private val missingConfigError = DocumentedResult(
        exitCode = ExitCode(13),
        meaning = "Configuration argument is required for this command",
    )
    override val errors: List<DocumentedResult> = listOf(
        invalidConfigError,
        missingConfigError,
    )
    private val action by argument().enum<Verb>()
    private val configuration by argument().optional()

    override suspend fun runCommand()
    {
        val configurationClass = if (configuration != null) {
            module.configurationModule
                .configurations
                .find { it.id == configuration }
                .also {
                    if (it == null) {
                        invalidConfigError.throwError()
                    }
                }
        } else null

        when (action) {
            Verb.Status -> {
                if (configurationClass == null) {
                    output.println("System Configurations", TextStyle.H1)
                    module.configurationModule.configurations.forEach {
                        output.println(it.id, TextStyle.H2)
                        printStatus(it.getStatus())
                    }
                } else {
                    printStatus(configurationClass.getStatus())
                }
            }
            Verb.Configure -> {
                if (configurationClass == null) {
                    missingConfigError.throwError()
                }
                configurationClass.configure()
            }
        }
    }

    private suspend fun printStatus(status: ConfigurationStatus)
    {
        output.print(StatusIndicatorElement(
            text = when (status) {
                is Configured -> "Configured"
                is NotConfigured -> "Not Configured"
                is Error -> "Error"
            },
            sentiment = when (status) {
                is Configured -> Sentiment.Positive
                is NotConfigured -> Sentiment.Nominal
                is Error -> Sentiment.Negative
            }
        ))
    }

    enum class Verb
    {
        Status,
        Configure,
    }
}
