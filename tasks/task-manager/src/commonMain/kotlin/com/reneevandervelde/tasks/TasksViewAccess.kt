package com.reneevandervelde.tasks

import ink.ui.structures.TextStyle
import ink.ui.structures.elements.TextElement
import ink.ui.structures.layouts.ScrollingListLayout
import ink.ui.structures.layouts.UiLayout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TasksViewAccess(
    taskData: TaskDataAccess,
) {
    val viewState: Flow<UiLayout> = taskData.latestTasks.map { tasks ->
        ScrollingListLayout(
            TextElement("All Tasks", TextStyle.H1),
            *tasks.map { TextElement(it.title, TextStyle.Body) }.toTypedArray()
        )
    }
}
