package com.reneevandervelde.system.render

import ink.ui.structures.elements.UiElement
import ink.ui.structures.render.RenderResult

interface ElementRenderer
{
    fun render(element: UiElement, parent: ElementRenderer): RenderResult
}
