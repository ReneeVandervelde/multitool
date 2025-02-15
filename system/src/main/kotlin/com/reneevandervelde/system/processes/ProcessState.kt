package com.reneevandervelde.system.processes

import com.reneevandervelde.system.FormattedLogger
import kotlinx.coroutines.flow.*
import java.io.File
import kotlin.streams.asSequence

/**
 * Result from an [exec] command.
 */
sealed interface ProcessState
{
    val commandString: String

    data class Prepared(
        override val commandString: String,
    ): ProcessState

    sealed interface Started: ProcessState

    data class Running(
        override val commandString: String,
    ): Started

    data class Capturing(
        override val commandString: String,
        val standardOutput: Flow<String>,
        val errorOutput: Flow<String>,
    ): Started {
        val output: Flow<String> get() = merge(standardOutput, errorOutput)
    }

    /**
     * Results that ran the command successfully.
     *
     * These results have outputs from the run command.
     */
    sealed interface Completed: Started
    {
        val exitCode: ExitCode
    }

    /**
     * Nominal return code from the executed command.
     */
    data class Success(
        override val commandString: String,
    ): Completed {
        override val exitCode: ExitCode = ExitCode.Success
    }

    /**
     * Result when the command was run, but exited with an unsuccesful code.
     */
    data class Failure(
        override val commandString: String,
        override val exitCode: ExitCode,
    ): Completed

    /**
     * Result when the command failed to run.
     *
     * This result type has no output and no exit code.
     */
    data class Error(
        override val commandString: String,
        val error: Throwable,
    ): ProcessState
}

/**
 * Execute a command and return the result.
 */
fun exec(
    vararg command: String,
    workingDir: File? = null,
    capture: Boolean = false,
): Flow<ProcessState> {
    return flow {
        val builder = ProcessBuilder(*command)
            .apply { if (!capture) inheritIO() }
            .apply { if (workingDir != null) directory(workingDir) }
        val commandString = builder.command().joinToString(" ")
        emit(ProcessState.Prepared(commandString))
        val processResult = runCatching {
            builder.start()
        }
        processResult.onFailure {
            emit(ProcessState.Error(commandString, it))
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
            emit(ProcessState.Capturing(
                commandString = commandString,
                standardOutput = standardOutput,
                errorOutput = errorOutput,
            ))
            process.waitFor()
            val exit = process.exitValue()
            if (exit == 0) {
                emit(ProcessState.Success(
                    commandString = commandString,
                ))
            } else {
                emit(ProcessState.Failure(
                    commandString = commandString,
                    exitCode = ExitCode(exit),
                ))
            }
        }
    }
}

val ProcessState.shortCommand: String get() = commandString.substringAfterLast('/').substringBefore(' ')

fun Flow<ProcessState>.printCapturedLines(
    prefix: String? = null
): Flow<ProcessState> {
    return onEach { state ->
        when (state) {
            is ProcessState.Capturing -> {
                val prefixString = "${prefix?.let { "$it > " }}${state.shortCommand}: "
                state.output.collect { println("$prefixString$it") }
            }
            else -> {}
        }
    }
}

fun Flow<ProcessState>.fenceOutput(logger: FormattedLogger): Flow<ProcessState>
{
    return onEach { state ->
        when (state) {
            is ProcessState.Prepared -> {
                logger.blank()
                logger.info("exec: ${state.commandString}")
                logger.divider()
            }
            is ProcessState.Completed -> {
                logger.divider()
                logger.info("exit: ${state.exitCode}")
                logger.blank()

            }
            is ProcessState.Error -> {
                logger.divider()
            }
            else -> {}
        }
    }
}

suspend fun Flow<ProcessState>.awaitSuccess(): ProcessState.Success
{
    return mapNotNull { state ->
        when (state) {
            is ProcessState.Error -> {
                throw ProcessError(state)
            }
            is ProcessState.Failure -> {
                throw RequiredProcessFailed(state)
            }
            is ProcessState.Success -> state
            else -> null
        }
    }.single()
}

