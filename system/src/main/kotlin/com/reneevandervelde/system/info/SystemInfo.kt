package com.reneevandervelde.system.info

import java.io.File

data class SystemInfo(
    val operatingSystem: OperatingSystem,
    val executables: List<File>,
) {
    fun hasCommand(command: String): Boolean
    {
        return executables.any { it.name == command }
    }

    fun missingCommand(command: String): Boolean
    {
        return !hasCommand(command)
    }
}

