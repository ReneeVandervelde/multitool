package com.reneevandervelde.system.render

import ink.ui.structures.TextStyle
import ink.ui.structures.elements.TextElement
import ink.ui.structures.elements.UiElement
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class TtyLayout
{
    private val renderRequests = Channel<UiElement>()
    val queue: Flow<UiElement> = renderRequests.consumeAsFlow()

    suspend fun print(element: UiElement)
    {
        renderRequests.send(element)
    }

    suspend fun close()
    {
        renderRequests.close()
        suspendCancellableCoroutine { continuation ->
            renderRequests.invokeOnClose {
                continuation.resume(Unit)
            }
        }
    }
}

suspend fun TtyLayout.println(message: String, textStyle: TextStyle = TextStyle.Body)
{
    print(TextElement(message, textStyle))
}
