package com.reneevandervelde.system.processes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import java.io.File
import kotlin.streams.asSequence
import kotlin.system.exitProcess

/**
 * Result from an [exec] command.
 */
sealed interface ProcessState
{
    sealed interface Started: ProcessState
    {
        val standardOutput: Flow<String>
        val errorOutput: Flow<String>
        val output: Flow<String> get() = merge(standardOutput, errorOutput)
    }

    data class Running(
        override val standardOutput: Flow<String>,
        override val errorOutput: Flow<String>,
    ): Started

    /**
     * Results that ran the command successfully.
     *
     * These results have outputs from the run command.
     */
    sealed interface Completed: Started
    {
        val exitCode: Int
    }

    /**
     * Nominal return code from the executed command.
     */
    data class Success(
        override val exitCode: Int,
        override val standardOutput: Flow<String>,
        override val errorOutput: Flow<String>,
    ): Completed

    /**
     * Result when the command was run, but exited with an unsuccesful code.
     */
    data class Failure(
        override val exitCode: Int,
        override val standardOutput: Flow<String>,
        override val errorOutput: Flow<String>,
    ): Completed

    /**
     * Result when the command failed to run.
     *
     * This result type has no output, and always has a failed exit code
     * of 127.
     */
    data class Error(
        val error: Throwable,
    ): ProcessState
}

/**
 * Execute a command and return the result.
 */
fun exec(
    vararg command: String,
    workingDir: File? = null,
): Flow<ProcessState> {
    return flow {
        val processResult = runCatching {
            ProcessBuilder(*command)
                .inheritIO()
                .apply { if (workingDir != null) directory(workingDir) }
                .start()
        }
        processResult.onFailure {
            emit(ProcessState.Error(it))
        }
        processResult.onSuccess { process ->
            val standardOutput = process.inputStream.bufferedReader()
                .lines()
                .asSequence()
                .asFlow()
            val errorOutput = process.errorStream.bufferedReader()
                .lines()
                .asSequence()
                .asFlow()
            emit(ProcessState.Running(
                standardOutput = standardOutput,
                errorOutput = errorOutput,
            ))
            process.waitFor()
            val exit = process.exitValue()
            if (exit == 0) {
                emit(ProcessState.Success(
                    exitCode = exit,
                    standardOutput = standardOutput,
                    errorOutput = errorOutput,
                ))
            } else {
                emit(ProcessState.Failure(
                    exitCode = exit,
                    standardOutput = standardOutput,
                    errorOutput = errorOutput,
                ))
            }
        }
    }
}

suspend fun Flow<ProcessState>.printAndRequireSuccess()
{
    collect { state ->
        when (state) {
            is ProcessState.Running -> {
                state.output.collect { println(it) }
            }
            is ProcessState.Error -> {
                throw state.error
            }
            is ProcessState.Failure -> {
                exitProcess(state.exitCode)
            }
            is ProcessState.Success -> {}
        }
    }
}
