package com.reneevandervelde.system.render

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.terminal.Terminal
import ink.ui.structures.Sentiment
import ink.ui.structures.elements.StatusIndicatorElement
import ink.ui.structures.elements.UiElement
import ink.ui.structures.render.RenderResult
import kimchi.logger.KimchiLogger

class StatusRenderer(
    private val terminal: Terminal,
    private val logger: KimchiLogger,
): ElementRenderer {
    override fun render(element: UiElement, parent: ElementRenderer): RenderResult {
        if (element !is StatusIndicatorElement) return RenderResult.Skipped
        logger.trace("print: ${element.text}")

        when (element.sentiment) {
            Sentiment.Positive -> terminal.println(
                "${TextColors.green("•")} ${element.text}"
            )
            Sentiment.Nominal -> terminal.println(
                "• ${element.text}"
            )
            Sentiment.Negative -> terminal.println(
                "${TextColors.red("•")} ${element.text}"
            )
            Sentiment.Primary -> terminal.println(
                "${TextColors.magenta("•")} ${element.text}"
            )
            Sentiment.Caution -> terminal.println(
                "${TextColors.yellow("•")} ${element.text}"
            )
            Sentiment.Idle -> terminal.println(
                "${TextColors.gray("•")} ${element.text}"
            )
        }

        return RenderResult.Rendered
    }
}
