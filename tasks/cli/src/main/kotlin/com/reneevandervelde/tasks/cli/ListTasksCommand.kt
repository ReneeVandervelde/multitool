package com.reneevandervelde.tasks.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.reneevandervelde.tasks.ManagementModule
import com.reneevandervelde.tasks.NotionPropertiesConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object ListTasksCommand: CliktCommand()
{
    override fun run() {
        runBlocking {
            ManagementModule(
                configAccess = NotionPropertiesConfig,
            ).view.viewState.first()
                .also { echo(it) }
        }
    }
}
