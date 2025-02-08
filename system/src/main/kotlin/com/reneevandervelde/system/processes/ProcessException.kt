package com.reneevandervelde.system.processes

import com.reneevandervelde.system.exceptions.AppError
import com.reneevandervelde.system.exceptions.DocumentedResult

abstract class ProcessException(
    message: String,
    cause: Throwable? = null,
): AppError(
    message = message,
    cause = cause,
    result = DocumentedResult.Global.Unexpected,
) {
    abstract val state: ProcessState
}
