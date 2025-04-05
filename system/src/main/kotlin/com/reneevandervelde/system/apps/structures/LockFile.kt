package com.reneevandervelde.system.apps.structures

import java.util.*

@JvmInline
value class LockFile(
    val properties: Properties,
) {
    val sha1 get() = properties.getProperty("sha1")
    val url get() = properties.getProperty("url")

    companion object
    {
        fun fromResourceFile(path: String): LockFile
        {
            val lockResource = Unit.javaClass.getResourceAsStream(path)

            return Properties().apply {
                load(lockResource)
            }.let(::LockFile)
        }
    }
}
