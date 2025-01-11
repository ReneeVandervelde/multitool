package com.reneevandervelde.settings

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.util.Properties

internal class PropertiesFileSettings(
    val file: File = File(System.getProperty("user.home"), ".multitool.properties")
): MultitoolSettings {
    override val allSettings: Flow<Map<String, String?>> = flow {
        val properties = Properties()
        file.inputStream().use {
            properties.load(it)
        }
        emit(properties.stringPropertyNames().associateWith { properties.getProperty(it) })
    }
}

internal actual val PlatformSettings: MultitoolSettings = PropertiesFileSettings()
