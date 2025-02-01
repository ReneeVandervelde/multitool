package com.reneevandervelde.system

import com.reneevandervelde.settings.SettingsModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

object SystemModule
{
    val ioScope = CoroutineScope(Dispatchers.IO)
    val defaultScope = CoroutineScope(Dispatchers.Default)
    val settings = SettingsModule().settingsAccess
}

