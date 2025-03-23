package com.reneevandervelde.tasks

import com.reneevandervelde.notion.NotionBearerToken
import com.reneevandervelde.notion.database.DatabaseId
import kotlinx.coroutines.flow.Flow
import regolith.data.settings.SettingsAccess
import regolith.data.settings.observeSetting
import regolith.data.settings.structure.StringData

class NotionConfigDatabase(
    settings: SettingsAccess,
): NotionConfigAccess {
    override val apiKey: Flow<NotionBearerToken?> = settings.observeSetting(Settings.apiKey)
    override val databaseId: Flow<DatabaseId?> = settings.observeSetting(Settings.databaseId)

    object Settings
    {
        val apiKey = StringData(
            key = "notion.api.key",
            name = "Notion API Key",
            dataTransformer = NotionBearerTokenTransformer,
            defaultValue = null,
        )

        val databaseId = StringData(
            key = "notion.api.database",
            name = "Notion Database ID",
            dataTransformer = NotionDatabaseIdTransformer,
            defaultValue = null,
        )

        val all = listOf(
            apiKey,
            databaseId,
        )
    }
}
