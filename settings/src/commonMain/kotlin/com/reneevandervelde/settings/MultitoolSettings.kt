package com.reneevandervelde.settings

import kotlinx.coroutines.flow.Flow

interface MultitoolSettings
{
    val allSettings: Flow<Map<String, String?>>
}

internal expect val PlatformSettings: MultitoolSettings
