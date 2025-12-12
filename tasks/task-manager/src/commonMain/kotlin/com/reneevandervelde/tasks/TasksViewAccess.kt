package com.reneevandervelde.tasks

import com.inkapplications.coroutines.combinePair
import com.reneevandervelde.notion.page.PageId
import ink.ui.structures.TextStyle
import ink.ui.structures.elements.ProgressElement
import ink.ui.structures.elements.TextElement
import ink.ui.structures.layouts.CenteredElementLayout
import ink.ui.structures.layouts.ScrollingListLayout
import ink.ui.structures.layouts.UiLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class TasksViewAccess(
    private val taskData: TaskDataAccess,
    actionScope: CoroutineScope,
) {
    private val stateCache = MutableStateFlow<Map<PageId, TaskRowElement.State>>(emptyMap())

    fun refresh()
    {
        stateCache.value = emptyMap()
        taskData.refresh()
    }

    val viewState: Flow<UiLayout> = stateCache.combinePair(taskData.latestTasks).map { (cache, tasks) ->
        if (tasks != null) {
            ScrollingListLayout(
                TextElement("Tasks", TextStyle.H1),
                *tasks
                    .groupBy { it.timeframe }
                    .map { (timeframe, tasks) ->
                         timeframe to tasks.sortedWith(
                            compareBy<TaskPage> { it.dueDate ?: Instant.DISTANT_FUTURE.toLocalDateTime(TimeZone.UTC).date }
                                .thenByDescending { it.urgency }
                                .thenByDescending { it.impact }
                         )
                    }
                    .sortedBy { it.first }
                    .flatMap { (timeframe, tasks) ->
                        listOf(
                            TextElement(timeframe.dropWhile { !it.isLetter() }, TextStyle.H2),
                            *tasks.map { task ->
                                TaskRowElement(
                                    task = task,
                                    completeTask = {
                                        actionScope.launch {
                                            stateCache.update {
                                                it.toMutableMap().also { it.put(task.page.id, TaskRowElement.State.Completing) }
                                            }
                                            taskData.markDone(task.page.id)
                                            stateCache.update {
                                                it.toMutableMap().also { it.put(task.page.id, TaskRowElement.State.Completed) }
                                            }
                                        }
                                    },
                                    resetTask = {
                                        actionScope.launch {
                                            stateCache.update {
                                                it.toMutableMap().also { it.put(task.page.id, TaskRowElement.State.Resetting) }
                                            }
                                            taskData.markNotStarted(task.page.id)
                                            stateCache.update {
                                                it.toMutableMap().also { it.put(task.page.id, TaskRowElement.State.Todo) }
                                            }
                                        }
                                    },
                                    displayState = cache[task.page.id] ?: TaskRowElement.State.Todo
                                )
                            }.toTypedArray()
                        )
                    }
                    .toTypedArray()
            )
        } else {
            CenteredElementLayout(
                ProgressElement.Indeterminate(
                    caption = "Loading tasks...",
                )
            )
        }
    }
}
