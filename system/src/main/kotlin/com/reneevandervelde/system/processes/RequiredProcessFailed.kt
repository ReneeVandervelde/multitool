package com.reneevandervelde.system.processes

class RequiredProcessFailed(
    override val state: ProcessState.Failure,
): ProcessException(
    message = "Required process failed with exit code ${state.exitCode.code}; command: ${state.commandString}"
)
