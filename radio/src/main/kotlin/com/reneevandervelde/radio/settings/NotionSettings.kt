package com.reneevandervelde.radio.settings

import com.reneevandervelde.notion.NOTION_SETTINGS_API_KEY
import com.reneevandervelde.notion.NotionBearerToken
import com.reneevandervelde.notion.database.DatabaseId
import com.reneevandervelde.settings.MultitoolSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal data class NotionSettings(
    val apiToken: NotionBearerToken,
    val channelDatabaseId: DatabaseId,
    val talkgroupDatabaseId: DatabaseId,
)

internal val MultitoolSettings.radioNotionSettings: Flow<NotionSettings?>
    get() = allSettings.map {
        NotionSettings(
            apiToken = it[NOTION_SETTINGS_API_KEY]
                ?.let(::NotionBearerToken)
                ?: return@map null,
            channelDatabaseId = it["notion-radio-channel-database-id"]
                ?.let(::DatabaseId)
                ?: return@map null,
            talkgroupDatabaseId = it["notion-radio-talkgroup-database-id"]
                ?.let(::DatabaseId)
                ?: return@map null,
        )
    }
