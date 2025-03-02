package com.reneevandervelde.system.commands.configure

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.choice
import com.reneevandervelde.system.commands.SystemCommand
import com.reneevandervelde.system.exceptions.DocumentedResult
import com.reneevandervelde.system.info.systemSettings
import kotlinx.coroutines.flow.first
import java.nio.file.Files

object ConfigureCommand: SystemCommand() {
    override val errors: List<DocumentedResult> = emptyList()

    private val target by argument().choice(
        "self",
    )

    override suspend fun runCommand() {
        when (target) {
            "self" -> configureSelf()
        }

        logger.info("Configured")
    }

    private suspend fun configureSelf() {
        val settings = module.settings.systemSettings.first()
        settings.createDirs()

        logger.debug("Linking system bin")
        if (settings.hostBinSystemLink.exists()) {
            logger.debug("Removing existing link")
            Files.delete(settings.hostBinSystemLink.toPath())
        }
        Files.createSymbolicLink(
            settings.hostBinSystemLink.toPath(),
            settings.systemInstallBin.toPath()
        )
    }
}
