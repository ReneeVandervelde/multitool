package com.reneevandervelde.system.exceptions

class CompositeExceptionHandler(
    private val handlers: List<ExceptionHandler>
): ExceptionHandler {
    override fun handle(exception: Throwable): ExceptionHandleResult
    {
        handlers.forEach { handler ->
            val result = handler.handle(exception)
            if (result != ExceptionHandleResult.Unhandled) {
                return result
            }
        }
        return ExceptionHandleResult.Unhandled
    }
}
