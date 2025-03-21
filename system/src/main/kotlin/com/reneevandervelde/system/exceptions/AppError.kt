package com.reneevandervelde.system.exceptions

open class AppError(
    val result: DocumentedResult,
    message: String,
    cause: Throwable? = null,
): SimpleError(message, cause)
