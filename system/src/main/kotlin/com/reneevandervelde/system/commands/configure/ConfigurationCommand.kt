package com.reneevandervelde.system.commands.configure

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.enum
import com.reneevandervelde.system.commands.SystemCommand
import com.reneevandervelde.system.commands.configure.ConfigurationStatus.Configured
import com.reneevandervelde.system.commands.configure.ConfigurationStatus.Error
import com.reneevandervelde.system.commands.configure.ConfigurationStatus.NotConfigured
import com.reneevandervelde.system.exceptions.DocumentedResult
import com.reneevandervelde.system.processes.ExitCode
import ink.ui.structures.Sentiment
import ink.ui.structures.elements.StatusIndicatorElement

object ConfigurationCommand: SystemCommand()
{
    private val invalidConfigError = DocumentedResult(
        exitCode = ExitCode(12),
        meaning = "The specified configuration was not found",
    )
    override val errors: List<DocumentedResult> = listOf(invalidConfigError)
    private val action by argument().enum<Verb>()
    private val configuration by argument()

    override suspend fun runCommand()
    {
        val configurationClass = module.configurationModule
            .configurations
            .find { it.id == configuration }

        if (configurationClass == null) {
            invalidConfigError.throwError()
        }

        when (action) {
            Verb.Status -> {
                val status = configurationClass.getStatus()
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
            Verb.Configure -> configurationClass.configure()
        }
    }

    enum class Verb
    {
        Status,
        Configure,
    }
}
