package com.reneevandervelde.system.processes

import com.reneevandervelde.system.exceptions.AppError
import com.reneevandervelde.system.exceptions.DocumentedResult

class RequiredProcessFailed(
    val state: ProcessState.Failure,
): AppError(
    result = DocumentedResult.Global.Unexpected,
    message = "Required process failed with exit code ${state.exitCode.code}; command: ${state.commandString}"
)
