package com.reneevandervelde.system.exceptions

import kimchi.logger.KimchiLogger

class AppExceptionHandler(
    private val logger: KimchiLogger,
): ExceptionHandler {
    override fun handle(exception: Throwable): ExceptionHandleResult
    {
        return when (exception) {
            is AppError -> {
                exception.cause?.printStackTrace()
                logger.error(exception.message.orEmpty())
                ExceptionHandleResult.Exit(exception.result.exitCode)
            }
            else -> ExceptionHandleResult.Unhandled
        }
    }
}
