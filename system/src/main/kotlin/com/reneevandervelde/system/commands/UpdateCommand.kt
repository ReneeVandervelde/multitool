package com.reneevandervelde.system.commands

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.mordant.terminal.prompt
import com.reneevandervelde.system.SystemSettings
import com.reneevandervelde.system.exceptions.DocumentedResult
import com.reneevandervelde.system.processes.ExitCode
import com.reneevandervelde.system.processes.git.GitCommands
import com.reneevandervelde.system.processes.awaitSuccess
import com.reneevandervelde.system.processes.fenceOutput
import com.reneevandervelde.system.systemSettings
import kotlinx.coroutines.flow.first

object UpdateCommand: SystemCommand()
{
    private val SignatureConfirmationFailure = DocumentedResult(
        meaning = "Signature confirmation failed",
        exitCode = ExitCode(10),
    )
    private val SelfUpdateFailure = DocumentedResult(
        meaning = "Self update failed",
        exitCode = ExitCode(11),
    )
    override val errors: List<DocumentedResult> = listOf(SignatureConfirmationFailure)

    override fun help(context: Context): String
    {
        return "Update software packages managed by multitool"
    }

    override suspend fun runCommand()
    {
        logger.info("Updating system...")
        val settings = module.settings.systemSettings.first()
        settings.createDirs()

        SelfUpdateFailure.wrapping {
            if (!settings.buildGitDir.exists()) {
                cloneBuildRepository(settings)
            }
        }
    }

    private suspend fun cloneBuildRepository(settings: SystemSettings)
    {
        logger.info("Cloning repo for build")
        GitCommands.clone("https://github.com/ReneeVandervelde/multitool.git", settings.buildDir)
            .fenceOutput(logger)
            .awaitSuccess()
        val buildRepository = GitCommands(settings.buildDir)

        buildRepository.status().fenceOutput(logger).awaitSuccess()
        buildRepository.log(
            count = 8,
            showSignature = true,
            format = "Commit: %H%nDate: %ai%n",
        ).fenceOutput(logger).awaitSuccess()

        val confirmation = terminal.prompt("Confirm repository signatures (Y/n)")

        if (confirmation != "Y") {
            logger.info("Confirmation rejected by input. Deleting Repository.")
            settings.buildDir.deleteRecursively()
            SignatureConfirmationFailure.throwError()
        }
    }
}

