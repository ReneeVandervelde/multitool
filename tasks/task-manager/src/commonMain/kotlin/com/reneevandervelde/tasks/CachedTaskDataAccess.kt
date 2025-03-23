package com.reneevandervelde.tasks

import com.reneevandervelde.notion.NotionApi
import com.reneevandervelde.notion.NotionBearerToken
import com.reneevandervelde.notion.database.DatabaseId
import com.reneevandervelde.notion.database.DatabaseQuery
import com.reneevandervelde.notion.page.PageFilter
import com.reneevandervelde.notion.page.ValueFilter
import com.reneevandervelde.notion.queryDatabaseForAll
import kotlinx.coroutines.flow.*

class CachedTaskDataAccess(
    notionApi: NotionApi,
    notionConfigAccess: NotionConfigAccess,
): TaskDataAccess {
    private val apiKey = notionConfigAccess.apiKey.filterNotNull()
    private val databaseId = notionConfigAccess.databaseId.filterNotNull()
    private val latestCache: MutableStateFlow<TasksCache?> = MutableStateFlow(null)

    override val latestTasks: Flow<List<TaskPage>> = combine(apiKey, databaseId, latestCache) { apiKey, database, cache ->
        TasksCache(
            apiKey = apiKey,
            databaseId = database,
            result = cache?.result?.takeIf {
                cache.apiKey == apiKey && cache.databaseId == database
            },
        )
    }.mapLatest { cache ->
        cache.result ?: notionApi.queryDatabaseForAll(
            token = cache.apiKey,
            database = cache.databaseId,
            query = DatabaseQuery(
                filter = PageFilter.Status(
                    property = TaskPage.Properties.Complete,
                    filter = ValueFilter.Equals("Not started"),
                )
            )
        ).map { TaskPage(it) }.also {
            latestCache.value = cache.copy(
                result = it,
            )
        }
    }
}

private data class TasksCache(
    val apiKey: NotionBearerToken,
    val databaseId: DatabaseId,
    val result: List<TaskPage>?,
)
