package com.reneevandervelde.system.commands.update

import com.github.ajalt.clikt.core.Context
import com.reneevandervelde.system.apps.structures.toUpdateOperation
import com.reneevandervelde.system.commands.SystemCommand
import com.reneevandervelde.system.exceptions.DocumentedResult
import com.reneevandervelde.system.info.systemSettings
import com.reneevandervelde.system.processes.ShellCommand
import com.reneevandervelde.system.processes.awaitSuccess
import com.reneevandervelde.system.processes.exec
import ink.ui.structures.TextStyle
import ink.ui.structures.elements.println
import kotlinx.coroutines.flow.first
import kotlin.time.measureTime

object UpdateCommand: SystemCommand()
{
    override val errors: List<DocumentedResult> = listOf()

    override fun help(context: Context): String
    {
        return "Update software packages managed by multitool"
    }

    override suspend fun runCommand()
    {
        output.println("System Updates", TextStyle.H1)

        val time = measureTime {
            ShellCommand("sudo -v").exec().awaitSuccess()
            val settings = module.settings.systemSettings.first()
            settings.createDirs()

            module.operationRunner.run(module.appsModule.updatables.map { it.toUpdateOperation() })
        }
        output.println("System updated (completed in $time)")
    }
}
