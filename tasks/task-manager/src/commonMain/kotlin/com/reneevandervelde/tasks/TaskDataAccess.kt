package com.reneevandervelde.tasks

import kotlinx.coroutines.flow.Flow

interface TaskDataAccess
{
    val latestTasks: Flow<List<TaskPage>>
}
