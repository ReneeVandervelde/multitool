package com.reneevandervelde.tasks

import ink.ui.structures.TextStyle
import ink.ui.structures.elements.ProgressElement
import ink.ui.structures.elements.TextElement
import ink.ui.structures.layouts.CenteredElementLayout
import ink.ui.structures.layouts.ScrollingListLayout
import ink.ui.structures.layouts.UiLayout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class TasksViewAccess(
    taskData: TaskDataAccess,
) {
    val viewState: Flow<UiLayout> = taskData.latestTasks.map { tasks ->
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
                            *tasks.map {
                                TaskRowElement(
                                    task = it,
                                    completeTask = { taskData.markDone(it.page.id) },
                                    resetTask = { taskData.markNotStarted(it.page.id) },
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
