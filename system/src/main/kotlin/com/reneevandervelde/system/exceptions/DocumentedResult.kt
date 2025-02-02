package com.reneevandervelde.system.exceptions

import com.inkapplications.standard.throwCancels
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

    suspend fun <T> wrapping(block: suspend () -> T): T {
        val result = runCatching {
            block()
        }.throwCancels()

        return result.getOrElse { throwError(it) }
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
