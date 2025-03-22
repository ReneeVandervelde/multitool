package com.reneevandervelde.system.render

class TerminalRenderer(
    renderers: List<ElementRenderer> = emptyList(),
) {
    private val elementRenderer = CompositeRenderer(renderers)

    suspend fun render(layout: TtyLayout)
    {
        layout.queue.collect { element ->
            elementRenderer.render(element, elementRenderer)
        }
    }
}
