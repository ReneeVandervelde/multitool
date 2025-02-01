package com.reneevandervelde.system.processes.git

import com.reneevandervelde.system.processes.ProcessState
import com.reneevandervelde.system.processes.exec
import kotlinx.coroutines.flow.Flow
import java.io.File

class GitCommands(val repositoryPath: File)
{
    fun status(): Flow<ProcessState>
    {
        return exec("git", "status", workingDir = repositoryPath)
    }

    fun log(
        count: Int? = null,
        showSignature: Boolean = false,
        author: Array<String>? = null,
        format: String? = null,
    ): Flow<ProcessState> {
        val args = listOfNotNull(
            "git",
            "log",
            "--show-signature".takeIf { showSignature },
            *count?.let { listOf("-n", it.toString()) }.orEmpty().toTypedArray(),
            *author?.flatMap { listOf("--author", it) }.orEmpty().toTypedArray(),
            format?.let { "--pretty=format:$it" },
        )
        return exec(command = args.toTypedArray(), workingDir = repositoryPath)
    }

    companion object
    {
        fun clone(repositoryUrl: String, target: File): Flow<ProcessState>
        {
            return exec("git", "clone", repositoryUrl, target.absolutePath)
        }
    }
}
