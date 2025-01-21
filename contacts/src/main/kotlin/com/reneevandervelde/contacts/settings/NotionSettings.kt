package com.reneevandervelde.contacts.settings

import regolith.data.settings.structure.StringData

object NotionSettings
{
    private val apiKey = SensitiveStringData(
        key = "notion.api.key",
        name = "Notion API Key",
        dataTransformer = NotionBearerTokenTransformer,
        defaultValue = null,
    )

    private val databaseId = StringData(
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
