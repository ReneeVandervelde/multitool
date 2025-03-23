package com.reneevandervelde.tasks

import com.reneevandervelde.notion.NotionBearerToken
import com.reneevandervelde.notion.database.DatabaseId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.util.*

object NotionPropertiesConfig: NotionConfigAccess
{
    private val propertiesFile by lazy {
        Properties().apply {
            val file = File(System.getProperty("user.home"), ".multitool.properties")
            this.load(file.inputStream())
        }
    }
    override val apiKey: Flow<NotionBearerToken?> = flow {
        propertiesFile.getProperty("notion-api-token")
            ?.let { NotionBearerToken(it) }
            .also { emit(it) }
    }
    override val databaseId: Flow<DatabaseId?> = flow {
        propertiesFile.getProperty("notion-tasks-database-id")
            ?.let { DatabaseId(it) }
            .also { emit(it) }
    }
}
