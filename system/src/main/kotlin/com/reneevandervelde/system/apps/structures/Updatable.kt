package com.reneevandervelde.system.apps.structures

import com.reneevandervelde.system.processes.Decision
import com.reneevandervelde.system.processes.Operation

interface Updatable: App
{
    suspend fun update()
}

fun Updatable.toUpdateOperation(): Operation
{
    return object: Operation
    {
        override val name: String = "${this@toUpdateOperation::class.simpleName} Update"
        override suspend fun enabled(): Decision = this@toUpdateOperation.enabled()
        override suspend fun runOperation() = this@toUpdateOperation.update()
    }
}
