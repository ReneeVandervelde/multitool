package com.reneevandervelde.radio.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

object MainCommand: CliktCommand()
{
    init
    {
        subcommands(ChannelExportCommand)
    }

    override fun run() = Unit
}
