package com.reneevandervelde.tasks

import com.reneevandervelde.notion.NotionBearerToken
import com.reneevandervelde.notion.database.DatabaseId
import kotlinx.coroutines.flow.Flow

interface NotionConfigAccess
{
    val apiKey: Flow<NotionBearerToken?>
    val databaseId: Flow<DatabaseId?>
}
