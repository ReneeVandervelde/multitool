package com.reneevandervelde.system.exceptions

import com.reneevandervelde.system.processes.ExitCode

sealed interface ExceptionHandleResult
{
    data object Unhandled: ExceptionHandleResult
    data object Ignore: ExceptionHandleResult
    data class Exit(val code: ExitCode): ExceptionHandleResult
}
