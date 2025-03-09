package com.reneevandervelde.system.apps.packagemanager

import com.reneevandervelde.system.apps.App
import com.reneevandervelde.system.apps.Updatable

interface PackageManager: App, Updatable
{
    //TODO: suspend fun status(appId: AppId)
    //TODO: suspend fun install(appId: AppId)
    //TODO: suspend fun uninstall(appId: AppId)
}
