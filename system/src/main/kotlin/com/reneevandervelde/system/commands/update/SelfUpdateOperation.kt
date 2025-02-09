package com.reneevandervelde.system.commands.update

import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.terminal.prompt
import com.reneevandervelde.settings.MultitoolSettings
import com.reneevandervelde.system.exceptions.simpleError
import com.reneevandervelde.system.info.SystemSettings
import com.reneevandervelde.system.info.systemSettings
import com.reneevandervelde.system.processes.*
import com.reneevandervelde.system.processes.git.GitRepository
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.first

class SelfUpdateOperation(
    private val settings: MultitoolSettings,
    private val terminal: Terminal,
    private val logger: KimchiLogger,
): Operation {
    override val name: String = "Self Update"

    override suspend fun enabled(): Decision {
        return Decision.Yes("Always enabled")
    }

    override suspend fun runOperation() {
        val settings = settings.systemSettings.first()
        if (!settings.buildGitDir.exists()) {
            cloneBuildRepository(settings)
        }
        val buildGitRepository = GitRepository(settings.buildDir)

        logger.info("Pulling latest changes from build repository")
        buildGitRepository.pull().exec(capture = true).printCapturedLines(name).awaitSuccess()

        logger.info("Installing latest version")
        exec("bin/install", workingDir = settings.buildDir, capture = true).printCapturedLines(name).awaitSuccess()
    }

    private suspend fun cloneBuildRepository(settings: SystemSettings)
    {
        logger.info("Cloning repo for build")
        GitRepository.clone("https://github.com/ReneeVandervelde/multitool.git", settings.buildDir)
            .exec(capture = true)
            .printCapturedLines(name)
            .awaitSuccess()
        val gitRepository = GitRepository(settings.buildDir)

        gitRepository.status().exec(capture = true).printCapturedLines(name).awaitSuccess()
        gitRepository.log(
            count = 8,
            showSignature = true,
            format = "Commit: %H%nDate: %ai%n",
        ).exec(capture = true).printCapturedLines(name).awaitSuccess()

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
