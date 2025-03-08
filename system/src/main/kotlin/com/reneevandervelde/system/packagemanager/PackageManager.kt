package com.reneevandervelde.system.packagemanager

import com.reneevandervelde.system.processes.Decision

interface PackageManager
{
    suspend fun enabled(): Decision
    suspend fun updateAll()
    //TODO: suspend fun status(appId: AppId)
    //TODO: suspend fun install(appId: AppId)
    //TODO: suspend fun uninstall(appId: AppId)
}
