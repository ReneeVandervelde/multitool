package com.reneevandervelde.tasks.android

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ink.ui.render.compose.renderer.ElementRenderer
import ink.ui.render.compose.theme.ComposeRenderTheme
import ink.ui.structures.elements.UiElement
import ink.ui.structures.render.RenderResult

data class TextInputElement(
    val label: String,
    val value: String,
    val onTextChanged: (String) -> Unit,
    val censored: Boolean = false,
): UiElement.Interactive {
    object Renderer: ElementRenderer
    {
        @Composable
        override fun render(
            element: UiElement,
            theme: ComposeRenderTheme, parent: ElementRenderer
        ): RenderResult {
            if (element !is TextInputElement) {
                return RenderResult.Skipped
            }

            Column(
                modifier = Modifier
                    .padding(vertical = theme.spacing.item)
                    .border(1.dp, theme.colors.foreground, shape = RoundedCornerShape(theme.sizing.corners))
                    .padding(theme.spacing.item)
            ) {
                BasicText(
                    text = element.label,
                    style = theme.typography.caption.copy(
                        color = theme.colors.foreground,
                    ),
                )
                BasicTextField(
                    value = if (element.censored) {
                        "*".repeat(element.value.length)
                    } else {
                        element.value
                    },
                    textStyle = theme.typography.body.copy(
                        color = theme.colors.foreground,
                    ),
                    onValueChange = element.onTextChanged,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            return RenderResult.Rendered
        }
    }
}

