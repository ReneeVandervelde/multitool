package com.reneevandervelde.tasks

import com.reneevandervelde.notion.NotionApi
import com.reneevandervelde.notion.database.DatabaseQuery
import com.reneevandervelde.notion.page.PageFilter
import com.reneevandervelde.notion.page.ValueFilter
import com.reneevandervelde.notion.queryDatabaseForAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull

class CachedTaskDataAccess(
    notionApi: NotionApi,
    notionConfigAccess: NotionConfigAccess,
): TaskDataAccess {
    private val apiKey = notionConfigAccess.apiKey.filterNotNull()
    private val databaseId = notionConfigAccess.databaseId.filterNotNull()

    override val latestTasks: Flow<List<TaskPage>> = combine(apiKey, databaseId) { apiKey, database ->
        notionApi.queryDatabaseForAll(
            token = apiKey,
            database = database,
            query = DatabaseQuery(
                filter = PageFilter.Status(
                    property = TaskPage.Properties.Complete,
                    filter = ValueFilter.Equals("Not started"),
                )
            )
        ).map { TaskPage(it) }
    }
}
