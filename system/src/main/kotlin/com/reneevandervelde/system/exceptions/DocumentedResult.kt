package com.reneevandervelde.system.exceptions

import com.reneevandervelde.system.processes.ExitCode

data class DocumentedResult(
    val exitCode: ExitCode,
    val meaning: String,
) {
    fun throwError(cause: Throwable? = null): Nothing {
        throw AppError(
            result = this,
            message = meaning,
            cause = cause,
        )
    }

    object Global
    {
        val Success = DocumentedResult(
            exitCode = ExitCode.Success,
            meaning = "The operation was successful"
        )
        val Unknown = DocumentedResult(
            exitCode = ExitCode.Unknown,
            meaning = "An unknown error occurred"
        )

        fun values() = listOf(Success, Unknown)
    }
}
