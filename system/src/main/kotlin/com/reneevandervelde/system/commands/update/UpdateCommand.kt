package com.reneevandervelde.system.commands.update

import com.github.ajalt.clikt.core.Context
import com.reneevandervelde.system.apps.toUpdateOperation
import com.reneevandervelde.system.commands.SystemCommand
import com.reneevandervelde.system.exceptions.DocumentedResult
import com.reneevandervelde.system.info.systemSettings
import com.reneevandervelde.system.processes.ShellCommand
import com.reneevandervelde.system.processes.awaitSuccess
import com.reneevandervelde.system.processes.exec
import kotlinx.coroutines.flow.first

object UpdateCommand: SystemCommand()
{
    override val errors: List<DocumentedResult> = listOf()

    override fun help(context: Context): String
    {
        return "Update software packages managed by multitool"
    }

    override suspend fun runCommand()
    {
        ShellCommand("sudo -v").exec().awaitSuccess()
        logger.info("Updating system...")
        val settings = module.settings.systemSettings.first()
        settings.createDirs()

        module.operationRunner.run(module.appsModule.updatables.map { it.toUpdateOperation() })
        logger.info("System updated")
    }
}
