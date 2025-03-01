package com.reneevandervelde.system.processes.git

import com.reneevandervelde.system.processes.ProcessState
import com.reneevandervelde.system.processes.ShellCommand
import com.reneevandervelde.system.processes.awaitSuccess
import com.reneevandervelde.system.processes.exec
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
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

    suspend fun getHash(): String
    {
        return coroutineScope {
            val args = listOf("git", "rev-parse", "HEAD")
            val processState = ShellCommand(command = args.toTypedArray(), workingDir = repositoryPath)
                .exec(capture = true)
            val output = async {
                processState
                    .filterIsInstance<ProcessState.Capturing>()
                    .single()
                    .output
                    .first()
            }
            processState.awaitSuccess()

            output.await()
        }
    }

    companion object
    {
        fun clone(repositoryUrl: String, target: File): ShellCommand
        {
            return ShellCommand("git", "clone", repositoryUrl, target.absolutePath)
        }
    }
}
