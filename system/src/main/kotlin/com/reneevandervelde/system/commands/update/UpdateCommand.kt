package com.reneevandervelde.system.commands.update

import com.github.ajalt.clikt.core.Context
import com.reneevandervelde.system.commands.SystemCommand
import com.reneevandervelde.system.exceptions.DocumentedResult
import com.reneevandervelde.system.info.systemSettings
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
        logger.info("Updating system...")
        val settings = module.settings.systemSettings.first()
        settings.createDirs()

        module.operationRunner.run(module.updateModule.operations)
        logger.info("System updated")
    }
}
