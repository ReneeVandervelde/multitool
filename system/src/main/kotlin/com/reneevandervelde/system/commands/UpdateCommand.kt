package com.reneevandervelde.system.commands

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.mordant.terminal.prompt
import com.reneevandervelde.system.SystemModule
import com.reneevandervelde.system.SystemSettings
import com.reneevandervelde.system.processes.git.GitCommands
import com.reneevandervelde.system.processes.printAndRequireSuccess
import com.reneevandervelde.system.systemSettings
import kotlinx.coroutines.flow.first
import kotlin.system.exitProcess

object UpdateCommand: SystemCommand()
{
    override fun help(context: Context): String
    {
        return "Update software packages managed by multitool"
    }

    override suspend fun runCommand()
    {
        println("Updating system...")
        val settings = SystemModule.settings.systemSettings.first()
        settings.createDirs()

        if (!settings.buildGitDir.exists()) {
            cloneBuildRepository(settings)
        }
    }

    private suspend fun cloneBuildRepository(settings: SystemSettings)
    {
        println("Cloning repo for build")
        val confirmation = runCatching {
            GitCommands.clone("https://github.com/ReneeVandervelde/multitool.git", settings.buildDir)
                .printAndRequireSuccess()
            val buildRepository = GitCommands(settings.buildDir)

            buildRepository.status().printAndRequireSuccess()
            buildRepository.log(
                count = 8,
                showSignature = true,
                format = "Commit: %H%nDate: %ai%n",
            ).printAndRequireSuccess()

            terminal.prompt("Confirm repository signatures (Y/n)")
        }

        if (confirmation.getOrNull() != "Y") {
            settings.buildDir.deleteRecursively()
            exitProcess(1)
        }
    }
}

