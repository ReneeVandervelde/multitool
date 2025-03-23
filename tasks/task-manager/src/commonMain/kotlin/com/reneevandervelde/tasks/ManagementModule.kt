package com.reneevandervelde.tasks

import com.reneevandervelde.notion.NotionModule

class ManagementModule(
    val configAccess: NotionConfigAccess,
) {
    val taskData = CachedTaskDataAccess(
        notionApi = NotionModule().client,
        notionConfigAccess = configAccess,
    )
    val view = TasksViewAccess(taskData)
}
