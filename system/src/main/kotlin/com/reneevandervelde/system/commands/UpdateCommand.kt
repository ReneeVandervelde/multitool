package com.reneevandervelde.system.commands

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.mordant.terminal.prompt
import com.inkapplications.standard.throwCancels
import com.reneevandervelde.system.info.SystemSettings
import com.reneevandervelde.system.exceptions.DocumentedResult
import com.reneevandervelde.system.exceptions.SimpleError
import com.reneevandervelde.system.exceptions.simpleError
import com.reneevandervelde.system.info.OperatingSystem
import com.reneevandervelde.system.processes.git.GitCommands
import com.reneevandervelde.system.info.systemSettings
import com.reneevandervelde.system.processes.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
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
        val systemInfo = module.ioScope.async {
            module.systemInfo.getSystemInfo()
        }

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

        val osTreeUpdates = runAsync {
            if (systemInfo.await().operatingSystem is OperatingSystem.Linux.Fedora.Silverblue) {
                logger.info("Installing ostree updates")
                exec("rpm-ostree", "upgrade", capture = true).printCapturedLines().awaitSuccess()
            }
        }
        val flatpakUpdates = runAsync {
            if (systemInfo.await().operatingSystem is OperatingSystem.Linux) {
                logger.info("Installing flatpak updates")
                exec("flatpak", "update", "-y", capture = true).printCapturedLines().awaitSuccess()
            }
        }

        val jobs = setOf(osTreeUpdates, flatpakUpdates)
        val results = jobs.awaitAll()
        val failures = results.filter { it.isFailure }
        when {
             failures.size == 1 -> {
                throw failures.single().exceptionOrNull()!!
            }
            failures.size > 1 -> {
                throw SimpleError("Multiple failures occurred")
            }
        }
        logger.info("System updated")
    }

    private fun runAsync(operation: suspend () -> Unit): Deferred<Result<Unit>>
    {
        return module.defaultScope.async {
            runCatching {
                operation()
            }.throwCancels().onFailure {
                val message = it.message ?: it.toString()
                when (it) {
                    is ProcessException -> logger.error("[${it.state.shortCommand}] $message")
                    else -> logger.error("Error while running background task", it)
                }

            }
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

