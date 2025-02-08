package com.reneevandervelde.system.exceptions

interface ExceptionHandler
{
    fun handle(exception: Throwable): ExceptionHandleResult
}
