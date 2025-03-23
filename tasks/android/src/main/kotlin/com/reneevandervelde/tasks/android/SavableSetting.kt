package com.reneevandervelde.tasks.android

import ink.ui.structures.elements.UiElement

data class SavableSetting(
    val uiElement: UiElement,
    val onSave: suspend () -> Unit,
)
