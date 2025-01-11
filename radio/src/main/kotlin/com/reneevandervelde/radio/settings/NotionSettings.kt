package com.reneevandervelde.radio.settings

import com.reneevandervelde.notion.NotionBearerToken
import com.reneevandervelde.notion.database.DatabaseId
import com.reneevandervelde.settings.MultitoolSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal data class NotionSettings(
    val apiToken: NotionBearerToken,
    val databaseId: DatabaseId,
)

internal val MultitoolSettings.radioNotionSettings: Flow<NotionSettings?>
    get() = allSettings.map {
        NotionSettings(
            apiToken = it["notion-api-token"]
                ?.let(::NotionBearerToken)
                ?: return@map null,
            databaseId = it["notion-radio-database-id"]
                ?.let(::DatabaseId)
                ?: return@map null,
        )
    }
