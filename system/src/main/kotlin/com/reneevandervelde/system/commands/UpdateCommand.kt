package com.reneevandervelde.system.commands

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.mordant.terminal.prompt
import com.reneevandervelde.system.SystemSettings
import com.reneevandervelde.system.exceptions.DocumentedResult
import com.reneevandervelde.system.exceptions.simpleError
import com.reneevandervelde.system.processes.ExitCode
import com.reneevandervelde.system.processes.git.GitCommands
import com.reneevandervelde.system.processes.awaitSuccess
import com.reneevandervelde.system.processes.exec
import com.reneevandervelde.system.processes.fenceOutput
import com.reneevandervelde.system.systemSettings
import kotlinx.coroutines.flow.first

object UpdateCommand: SystemCommand()
{
    private val SelfUpdateFailure = DocumentedResult(
        meaning = "Self update failed",
        exitCode = ExitCode(10),
    )
    override val errors: List<DocumentedResult> = listOf(
        SelfUpdateFailure,
    )

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
            val buildGitRepository = GitCommands(settings.buildDir)

            logger.info("Pulling latest changes from build repository")
            buildGitRepository.pull().fenceOutput(logger).awaitSuccess()

            logger.info("Installing latest version")
            exec("bin/install", workingDir = settings.buildDir).fenceOutput(logger).awaitSuccess()
        }
    }

    private suspend fun cloneBuildRepository(settings: SystemSettings)
    {
        logger.info("Cloning repo for build")
        GitCommands.clone("https://github.com/ReneeVandervelde/multitool.git", settings.buildDir)
            .fenceOutput(logger)
            .awaitSuccess()
        val gitRepository = GitCommands(settings.buildDir)

        gitRepository.status().fenceOutput(logger).awaitSuccess()
        gitRepository.log(
            count = 8,
            showSignature = true,
            format = "Commit: %H%nDate: %ai%n",
        ).fenceOutput(logger).awaitSuccess()

        val confirmation = terminal.prompt("Confirm repository signatures (Y/n)")

        if (confirmation != "Y") {
            logger.info("Confirmation rejected by input. Deleting Repository.")
            settings.buildDir.deleteRecursively()
            simpleError("Signature confirmation failed")
        } else {
            logger.info("Signature Verified.")
        }
    }
}

