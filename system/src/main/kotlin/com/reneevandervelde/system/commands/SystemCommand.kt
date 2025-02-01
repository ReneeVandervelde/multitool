package com.reneevandervelde.system.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.reneevandervelde.system.SystemModule
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

abstract class SystemCommand: CliktCommand()
{
    val module = SystemModule

    final override fun run()
    {
        runBlocking {
            runCatching {
                runCommand()
            }.onFailure { error ->
                error.printStackTrace()
                exitProcess(1)
            }
        }
    }

    abstract suspend fun runCommand()
}
