package com.reneevandervelde.system.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

object MainCommand: CliktCommand()
{
    init
    {
        subcommands(UpdateCommand)
    }

    override fun run() = Unit
}


