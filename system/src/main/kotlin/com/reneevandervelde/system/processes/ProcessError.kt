package com.reneevandervelde.system.processes

import com.reneevandervelde.system.exceptions.AppError
import com.reneevandervelde.system.exceptions.DocumentedResult

class ProcessError(
    val state: ProcessState.Error,
): AppError(
    message = "Process failed to run with error: ${state.error.message}; command: ${state.commandString}",
    cause = state.error,
    result = DocumentedResult.Global.Unexpected,
)
