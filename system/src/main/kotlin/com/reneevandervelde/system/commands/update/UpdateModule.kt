package com.reneevandervelde.system.commands.update

import com.reneevandervelde.system.apps.AppsModule
import com.reneevandervelde.system.apps.updateOperation
import com.reneevandervelde.system.packagemanager.PackageManager
import com.reneevandervelde.system.processes.Decision
import com.reneevandervelde.system.processes.Operation

class UpdateModule(
    appsModule: AppsModule,
) {
    val operations: List<Operation> = listOf(
        appsModule.multitoolSelf.updateOperation,
        *appsModule.packageManagers.map { it.toUpdateOperation() }.toTypedArray(),
    )
}

fun PackageManager.toUpdateOperation(): Operation {
    return object: Operation {
        override val name: String = this::class.simpleName.toString()
        override suspend fun enabled(): Decision = this@toUpdateOperation.enabled()
        override suspend fun runOperation() = updateAll()
    }
}
