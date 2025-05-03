package com.reneevandervelde.tasks

import com.reneevandervelde.notion.NotionModule
import kotlinx.coroutines.CoroutineScope

class ManagementModule(
    val configAccess: NotionConfigAccess,
    val ioScope: CoroutineScope,
) {
    val taskData = CachedTaskDataAccess(
        notionApi = NotionModule().client,
        notionConfigAccess = configAccess,
    )
    val view = TasksViewAccess(
        taskData = taskData,
        actionScope = ioScope
    )
}
