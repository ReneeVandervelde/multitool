package com.reneevandervelde.tasks

import ink.ui.structures.elements.UiElement

data class TaskRowElement(
    val task: TaskPage,
    val completeTask: () -> Unit,
    val resetTask: () -> Unit,
    val displayState: State = State.Todo,
): UiElement.Interactive {
    enum class State {
        Todo,
        Completing,
        Completed,
        Resetting,
    }
}
