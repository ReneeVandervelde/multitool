package com.reneevandervelde.contacts.settings

import ink.ui.structures.elements.UiElement

data class SavableSetting(
    val uiElement: UiElement,
    val onSave: suspend () -> Unit,
)
