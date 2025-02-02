package com.reneevandervelde.system.exceptions

import kimchi.logger.KimchiLogger

class AppExceptionHandler(
    private val logger: KimchiLogger,
): ExceptionHandler {
    override fun handle(exception: Throwable): ExceptionHandleResult
    {
        return when (exception) {
            is AppError -> handleAppError(exception)
            else -> ExceptionHandleResult.Unhandled
        }
    }

    private fun handleAppError(error: AppError): ExceptionHandleResult
    {
        val result = if (error.cause is AppError) {
            handleAppError(error.cause as AppError)
        } else {
            ExceptionHandleResult.Exit(error.result.exitCode)
        }

        logger.error(error.message.orEmpty())
        error.cause.takeIf { it !is AppError }?.printStackTrace()

        return result
    }
}
