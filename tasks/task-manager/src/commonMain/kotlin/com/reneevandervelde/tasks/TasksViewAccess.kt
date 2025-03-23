package com.reneevandervelde.tasks

import ink.ui.structures.TextStyle
import ink.ui.structures.elements.ProgressElement
import ink.ui.structures.elements.TextElement
import ink.ui.structures.layouts.CenteredElementLayout
import ink.ui.structures.layouts.ScrollingListLayout
import ink.ui.structures.layouts.UiLayout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TasksViewAccess(
    taskData: TaskDataAccess,
) {
    val viewState: Flow<UiLayout> = taskData.latestTasks.map { tasks ->
        if (tasks != null) {
            ScrollingListLayout(
                TextElement("All Tasks", TextStyle.H1),
                *tasks.map {
                    TaskRowElement(it)
                }.toTypedArray()
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
