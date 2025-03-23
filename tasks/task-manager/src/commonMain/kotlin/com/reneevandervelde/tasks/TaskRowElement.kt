package com.reneevandervelde.tasks

import ink.ui.structures.elements.UiElement

data class TaskRowElement(
    val task: TaskPage,
    val completeTask: suspend () -> Unit,
    val resetTask: suspend () -> Unit,
): UiElement.Interactive
