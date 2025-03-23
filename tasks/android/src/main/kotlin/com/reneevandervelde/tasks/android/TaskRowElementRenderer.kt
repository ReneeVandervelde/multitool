package com.reneevandervelde.tasks.android

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.reneevandervelde.notion.page.PageIcon
import com.reneevandervelde.tasks.TaskRowElement
import ink.ui.render.compose.renderer.ElementRenderer
import ink.ui.render.compose.renderer.resource
import ink.ui.render.compose.theme.ComposeRenderTheme
import ink.ui.structures.Sentiment
import ink.ui.structures.Symbol
import ink.ui.structures.elements.UiElement
import ink.ui.structures.render.RenderResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

private enum class States {
    Todo,
    Completing,
    Completed,
    Resetting,
}

object TaskRowElementRenderer: ElementRenderer {
    @Composable
    override fun render(
        element: UiElement,
        theme: ComposeRenderTheme,
        parent: ElementRenderer
    ): RenderResult {
        if (element !is TaskRowElement) return RenderResult.Skipped
        var state by remember { mutableStateOf(States.Todo) }
        println(state)
        when (state) {
            States.Completing -> LaunchedEffect("complete-${element.task.page.id}", state) {
                println("Completing")
                element.completeTask()
                state = States.Completed
            }
            States.Resetting -> LaunchedEffect("reset-${element.task.page.id}", state) {
                println("Resetting")
                element.resetTask()
                state = States.Todo
            }
            else -> {}
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(vertical = theme.spacing.item)
        ) {
            BasicText(
                text = element.task.page.icon?.let { it as? PageIcon.Emoji }?.emoji ?: "\uD83D\uDCCB",
                style = theme.typography.uiGlyph,
            )
            BasicText(
                text = element.task.title,
                style = theme.typography.body.copy(
                    color = theme.colors.foreground,
                ),
                modifier = Modifier.weight(1f, true).padding(start = theme.spacing.item)
            )
            Button(
                text = "",
                leadingSymbol = Symbol.Done,
                sentiment = when (state) {
                    States.Todo -> Sentiment.Nominal
                    States.Completed -> Sentiment.Positive
                    States.Completing, States.Resetting -> Sentiment.Idle
                },
                theme = theme,
                onLongClick = {
                    when (state) {
                        States.Todo ->  state = States.Completing
                        States.Completed -> state = States.Resetting
                        else -> {}
                    }
                }.takeIf { state in setOf(States.Todo, States.Completed) },
            )
        }

        return RenderResult.Rendered
    }
}

// TODO: Move the longpress functionality added here into ink-ui
@Composable
private fun Button(
    text: String,
    sentiment: Sentiment = Sentiment.Nominal,
    latching: Boolean = false,
    leadingSymbol: Symbol? = null,
    trailingSymbol: Symbol? = null,
    theme: ComposeRenderTheme,
    onClick: () -> Unit = {},
    onLongClick: (() -> Unit)? = null,
    buttonModifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
) {
    var latched by remember(text, sentiment) { mutableStateOf(false) }
    val borderColor = if (latched) theme.colors.inactive else theme.colors.forSentiment(sentiment)
    val latchingClick = {
        if (latching) {
            latched = true
        }
        onClick()
    }
    val haptic = LocalHapticFeedback.current
    val backgroundColor = remember { Animatable(theme.colors.surface) }
    val scope = rememberCoroutineScope()
    var animationJob by remember { mutableStateOf<Job?>(null) }
    Box(
        contentAlignment = Alignment.Center,
        modifier = buttonModifier
            .semantics { role = Role.Button }
            .clip(RoundedCornerShape(theme.sizing.corners))
            .border(theme.sizing.borders, borderColor, RoundedCornerShape(theme.sizing.corners))
            .background(backgroundColor.value)
            .let { if (latched) it else it.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    animationJob?.cancel()
                    animationJob = scope.launch {
                        backgroundColor.snapTo(theme.colors.surfaceInteraction)
                        backgroundColor.animateTo(theme.colors.surface.copy(), tween(300))
                    }
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    latchingClick()
                }
            )}
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        onLongClick?.run {
                            animationJob?.cancel()
                            animationJob = scope.launch {
                                backgroundColor.snapTo(theme.colors.surfaceInteraction)
                                backgroundColor.animateTo(theme.colors.surface.copy(), tween(300))
                            }
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            invoke()
                        }
                    },
                )
            }
            .padding(theme.spacing.clickSafety)
            .then(contentModifier)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            if (leadingSymbol != null) {
                Image(
                    painterResource(leadingSymbol.resource),
                    colorFilter = ColorFilter.tint(theme.colors.forSentiment(sentiment)),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = theme.spacing.item.takeIf { text.isNotEmpty() } ?: 0.dp)
                        .size(theme.sizing.hintIcons),
                )
            }
            if (latched) {
                LaunchedEffect(latched) {
                    delay(10_000)
                    latched = false
                }
                BasicText(
                    text = "-".repeat(text.length),
                    style = theme.typography.body.copy(
                        color = theme.colors.foreground,
                    )
                )

            } else {
                BasicText(
                    text = text,
                    style = theme.typography.body.copy(
                        color = theme.colors.foreground,
                    )
                )
            }
            if (trailingSymbol != null) {
                Image(
                    painterResource(trailingSymbol.resource),
                    colorFilter = ColorFilter.tint(theme.colors.forSentiment(sentiment)),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = theme.spacing.item.takeIf { text.isNotEmpty() } ?: 0.dp)
                        .size(theme.sizing.hintIcons),
                )
            }
        }
    }
}
