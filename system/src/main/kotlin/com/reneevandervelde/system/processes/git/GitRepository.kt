package com.reneevandervelde.system.processes.git

import com.reneevandervelde.system.processes.ShellCommand
import java.io.File

class GitRepository(
    private val repositoryPath: File
) {
    fun status(): ShellCommand
    {
        return ShellCommand("git", "status", workingDir = repositoryPath)
    }

    fun log(
        count: Int? = null,
        showSignature: Boolean = false,
        author: Array<String>? = null,
        format: String? = null,
    ): ShellCommand {
        val args = listOfNotNull(
            "git",
            "log",
            "--show-signature".takeIf { showSignature },
            *count?.let { listOf("-n", it.toString()) }.orEmpty().toTypedArray(),
            *author?.flatMap { listOf("--author", it) }.orEmpty().toTypedArray(),
            format?.let { "--pretty=format:$it" },
        )
        return ShellCommand(command = args.toTypedArray(), workingDir = repositoryPath)
    }

    fun pull(
        fastForwardOnly: Boolean = true,
        verifySignatures: Boolean = true,
    ): ShellCommand {
        val args = listOfNotNull(
            "git",
            "pull",
            "--ff-only".takeIf { fastForwardOnly },
            "--verify-signatures".takeIf { verifySignatures },
        )
        return ShellCommand(command = args.toTypedArray(), workingDir = repositoryPath)
    }

    companion object
    {
        fun clone(repositoryUrl: String, target: File): ShellCommand
        {
            return ShellCommand("git", "clone", repositoryUrl, target.absolutePath)
        }
    }
}
