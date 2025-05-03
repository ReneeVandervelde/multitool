package com.reneevandervelde.tasks.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.reneevandervelde.tasks.ManagementModule
import com.reneevandervelde.tasks.NotionConfigDatabase
import ink.ui.render.compose.ComposeRenderer
import ink.ui.render.compose.theme.defaultTheme
import ink.ui.structures.elements.ButtonElement
import ink.ui.structures.layouts.ScrollingListLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import regolith.data.settings.AndroidSettingsModule

class TaskListActivity: ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        val settingsModule = AndroidSettingsModule(
            context = this,
        )
        val managementModule = ManagementModule(
            configAccess = NotionConfigDatabase(
                settings = settingsModule.settingsAccess,
            ),
            ioScope = CoroutineScope(Dispatchers.IO),
        )

        setContent {
            val renderer = ComposeRenderer(
                theme = defaultTheme(),
                renderers = listOf(
                    TextInputElement.Renderer,
                    TaskRowElementRenderer,
                )
            )
            val state by managementModule.view.viewState.collectAsState(null)
            if (state == null) {
                NotionConfigDatabase.Settings.all
                    .map {
                        StringSettingRow(
                            settingsAccess = settingsModule.settingsAccess,
                            setting = it.toPrimitive(),
                        )
                    }.run {
                        renderer.render(
                            ScrollingListLayout(
                                *map { it.uiElement }.toTypedArray(),
                                ButtonElement(
                                    text = "Save",
                                    onClick = {
                                        lifecycleScope.launch {
                                            map { async { it.onSave() } }.awaitAll()
                                        }
                                    },
                                ),
                            ),
                        )
                    }
            } else {
                renderer.render(
                    uiLayout = state ?: ScrollingListLayout()
                )
            }
        }
    }
}
