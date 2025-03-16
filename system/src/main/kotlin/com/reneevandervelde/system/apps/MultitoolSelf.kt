package com.reneevandervelde.system.apps

import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.terminal.prompt
import com.reneevandervelde.settings.MultitoolSettings
import com.reneevandervelde.system.exceptions.simpleError
import com.reneevandervelde.system.info.SystemSettings
import com.reneevandervelde.system.info.systemSettings
import com.reneevandervelde.system.processes.*
import com.reneevandervelde.system.render.TtyLayout
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.first

class MultitoolSelf(
    private val settings: MultitoolSettings,
    private val output: TtyLayout,
    private val terminal: Terminal,
    private val logger: KimchiLogger,
): Updatable {
    private val updateName = "Multitool Updates"

    override suspend fun enabled(): Decision {
        return Decision.Yes("Always Enabled")
    }

    override suspend fun update()
    {
        val settings = settings.systemSettings.first()
        val requiresInstall = !settings.multitoolGitDir.exists()
        if (requiresInstall) {
            cloneBuildRepository(settings)
        }
        val buildGitRepository = GitRepository(settings.multitoolBuildDir)
        val initialHash = buildGitRepository.getHash()

        logger.info("Pulling latest changes from build repository")
        buildGitRepository.pull().exec(capture = true)
            .printCapturedLines(output, updateName)
            .awaitSuccess()
        val updatedHash = buildGitRepository.getHash()

        if (!requiresInstall && initialHash == updatedHash) {
            logger.info("No changes detected in build repository")
            return
        }
        logger.info("Installing latest version")
        exec("bin/gradlew system:install", workingDir = settings.multitoolBuildDir, capture = true)
            .printCapturedLines(output, updateName)
            .awaitSuccess()
    }

    private suspend fun cloneBuildRepository(settings: SystemSettings)
    {
        logger.info("Cloning repo for build")
        GitRepository.clone("https://github.com/ReneeVandervelde/multitool.git", settings.multitoolBuildDir)
            .exec(capture = true)
            .printCapturedLines(output, updateName)
            .awaitSuccess()
        val gitRepository = GitRepository(settings.multitoolBuildDir)

        gitRepository.status().exec(capture = true).printCapturedLines(output, updateName).awaitSuccess()
        gitRepository.log(
            count = 8,
            showSignature = true,
            format = "Commit: %H%nDate: %ai%n",
        ).exec(capture = true).printCapturedLines(output, updateName).awaitSuccess()

        val confirmation = terminal.prompt("Confirm repository signatures (Y/n)")

        if (confirmation != "Y") {
            logger.info("Confirmation rejected by input. Deleting Repository.")
            settings.multitoolBuildDir.deleteRecursively()
            simpleError("Signature confirmation failed")
        } else {
            logger.info("Signature Verified.")
        }
    }
}

val MultitoolSelf.updateOperation get() = object: Operation {
    override val name: String = "Self Update"
    override suspend fun enabled(): Decision
    {
        return Decision.Yes("Always enabled")
    }
    override suspend fun runOperation() = update()
}
