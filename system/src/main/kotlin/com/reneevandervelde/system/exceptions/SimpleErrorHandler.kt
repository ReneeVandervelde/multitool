package com.reneevandervelde.system.exceptions

import com.reneevandervelde.system.processes.ExitCode
import kimchi.logger.KimchiLogger

class SimpleErrorHandler(
    private val logger: KimchiLogger,
): ExceptionHandler {
    override fun handle(exception: Throwable): ExceptionHandleResult
    {
        return when (exception) {
            is SimpleError -> handleSimpleError(exception)
            else -> ExceptionHandleResult.Unhandled
        }
    }

    private fun handleSimpleError(error: SimpleError): ExceptionHandleResult
    {
        if (error.cause is SimpleError) {
            handleSimpleError(error.cause as SimpleError)
        }

        logger.error(error.message.orEmpty(), error.cause.takeIf { it !is SimpleError })

        val exitCode = when (error) {
            is AppError -> error.result.exitCode
            else -> ExitCode.Unknown
        }

        return ExceptionHandleResult.Exit(exitCode)
    }
}
