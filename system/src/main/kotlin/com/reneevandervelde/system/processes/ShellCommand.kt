package com.reneevandervelde.system.processes

import kotlinx.coroutines.flow.Flow
import java.io.File

data class ShellCommand(
    val command: List<String>,
    val workingDir: File? = null,
) {
    constructor(vararg command: String, workingDir: File? = null): this(
        command = command.toList(),
        workingDir = workingDir,
    )

    constructor(command: String, workingDir: File? = null): this(
        command = command.split(" "),
        workingDir = workingDir,
    )
}

fun ShellCommand.exec(capture: Boolean = false): Flow<ProcessState>
{
    return exec(*command.toTypedArray(), workingDir = workingDir, capture = capture)
}
