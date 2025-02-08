package com.reneevandervelde.system.exceptions

open class SimpleError(
    message: String,
    cause: Throwable? = null,
): Error(message, cause)

fun simpleError(message: String): Nothing {
    throw SimpleError(message)
}
