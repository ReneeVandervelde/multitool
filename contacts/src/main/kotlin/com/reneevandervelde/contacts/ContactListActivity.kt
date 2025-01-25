package com.reneevandervelde.contacts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.reneevandervelde.contacts.settings.SensitiveStringData
import com.reneevandervelde.contacts.settings.StringSettingRow
import ink.ui.render.compose.ComposeRenderer
import ink.ui.render.compose.theme.defaultTheme
import ink.ui.structures.TextStyle
import ink.ui.structures.elements.ButtonElement
import ink.ui.structures.elements.TextElement
import ink.ui.structures.layouts.ScrollingListLayout
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import regolith.data.settings.structure.*

class ContactListActivity: ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        val module = ContactsModule(application)

        module.initRunner.initialize()

        setContent {
            val settingsRows = module.appSettings
                .map { setting ->
                    val primitiveSetting = when (setting) {
                        is PrimitiveSetting<*> -> setting
                        is DataSetting<*, *> -> setting.toPrimitive()
                    }
                    when {
                        setting is SensitiveStringData<*> -> StringSettingRow(
                            settingsAccess = module.settingsAccess,
                            setting = setting.toPrimitive(),
                            sensitive = true,
                        )
                        primitiveSetting is StringSetting -> StringSettingRow(
                            settingsAccess = module.settingsAccess,
                            setting = primitiveSetting
                        )
                        else -> TODO()
                    }
                }
            ComposeRenderer(
                theme = defaultTheme(),
                renderers = listOf(
                    TextInputElement.Renderer,
                )
            ).render(
                uiLayout = ScrollingListLayout(
                    items = listOf(
                        TextElement("Settings", style = TextStyle.H1),
                        *settingsRows.map { it.uiElement }.toTypedArray(),
                        ButtonElement(
                            text = "Save",
                            onClick = {
                                lifecycleScope.launch {
                                    settingsRows.map { async { it.onSave() } }
                                        .awaitAll()
                                }
                            },
                        ),
                    ),
                ),
            )
        }
    }
}
