package com.reneevandervelde.system.processes

class ProcessError(
    override val state: ProcessState.Error,
): ProcessException(
    message = "Process failed to run with error: ${state.error.message}; command: ${state.commandString}",
    cause = state.error,
)
