package com.reneevandervelde.contacts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ink.ui.render.compose.ComposeRenderer
import ink.ui.render.compose.theme.defaultTheme
import ink.ui.structures.elements.TextElement
import ink.ui.structures.layouts.CenteredElementLayout

class ContactListActivity: ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeRenderer(
                theme = defaultTheme(),
            ).render(
                uiLayout = CenteredElementLayout(
                    body = TextElement("Hello, World!"),
                ),
            )
        }
    }
}
