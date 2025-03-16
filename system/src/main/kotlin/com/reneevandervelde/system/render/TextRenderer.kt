package com.reneevandervelde.system.render

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles
import com.github.ajalt.mordant.terminal.Terminal
import ink.ui.structures.TextStyle
import ink.ui.structures.elements.TextElement
import ink.ui.structures.elements.UiElement
import ink.ui.structures.render.RenderResult
import kimchi.logger.KimchiLogger

class TextRenderer(
    private val terminal: Terminal,
    private val logger: KimchiLogger,
): ElementRenderer {
    override fun render(element: UiElement, parent: ElementRenderer): RenderResult
    {
        if (element !is TextElement) return RenderResult.Skipped
        logger.trace("print: ${element.text}")

        when (element.style) {
            TextStyle.H1 -> terminal.println(
                TextStyles.bold("# ${element.text}")
            )
            TextStyle.H2 -> terminal.println(
                TextColors.blue(TextStyles.bold("## ${element.text}"))
            )
            TextStyle.H3 -> terminal.println(
                TextColors.blue("### ${element.text}")
            )
            TextStyle.Caption -> terminal.println(
                TextColors.gray("(${element.text})")
            )
            TextStyle.Body -> terminal.println(element.text)
        }

        return RenderResult.Rendered
    }
}
