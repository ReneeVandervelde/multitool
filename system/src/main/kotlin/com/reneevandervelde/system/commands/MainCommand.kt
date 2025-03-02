package com.reneevandervelde.system.commands

import com.github.ajalt.clikt.core.*
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.Theme
import com.github.ajalt.mordant.terminal.Terminal
import com.reneevandervelde.system.commands.configure.ConfigureCommand
import com.reneevandervelde.system.commands.update.UpdateCommand

object MainCommand: NoOpCliktCommand()
{
    init
    {
        context {
            terminal = Terminal(
                theme = Theme {
                    styles["info"] = TextColors.magenta
                    styles["warning"] = TextColors.yellow
                    styles["danger"] = TextColors.red
                    styles["muted"] = TextColors.gray
                }
            )
        }

        subcommands(
            UpdateCommand,
            ConfigureCommand,
        )
    }
}


