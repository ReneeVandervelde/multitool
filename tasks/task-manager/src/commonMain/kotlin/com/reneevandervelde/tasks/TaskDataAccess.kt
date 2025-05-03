package com.reneevandervelde.tasks

import com.reneevandervelde.notion.page.PageId
import kotlinx.coroutines.flow.Flow

interface TaskDataAccess
{
    val latestTasks: Flow<List<TaskPage>?>
    suspend fun markDone(task: PageId)
    suspend fun markNotStarted(task: PageId)
    fun refresh()
}
