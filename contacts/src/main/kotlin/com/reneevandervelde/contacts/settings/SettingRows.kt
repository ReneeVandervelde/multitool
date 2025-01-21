package com.reneevandervelde.contacts.settings

import androidx.compose.runtime.*
import com.reneevandervelde.contacts.TextInputElement
import kotlinx.coroutines.flow.collectLatest
import regolith.data.settings.SettingsAccess
import regolith.data.settings.structure.PrimitiveSetting

@Composable
fun StringSettingRow(
    settingsAccess: SettingsAccess,
    setting: PrimitiveSetting<String?>,
    sensitive: Boolean = false,
): SavableSetting {
    var current by remember { mutableStateOf("") }
    LaunchedEffect(null) {
        settingsAccess.observeSetting(setting).collectLatest {
            current = it.orEmpty()
        }
    }

    return SavableSetting(
        uiElement = TextInputElement(
            label = setting.name,
            value = current,
            onTextChanged = {
                current = it
            },
            censored = sensitive,
        ),
        onSave = {
            settingsAccess.writeSetting(
                setting,
                current.takeIf { it.isNotBlank() }
            )
        }
    )
}

