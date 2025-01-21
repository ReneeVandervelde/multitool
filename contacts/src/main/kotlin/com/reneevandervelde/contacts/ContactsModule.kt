package com.reneevandervelde.contacts

import android.app.Application
import com.reneevandervelde.contacts.settings.NotionSettings
import regolith.data.settings.AndroidSettingsModule
import regolith.data.settings.structure.Setting
import regolith.data.settings.structure.StringData

class ContactsModule(
    application: Application,
) {
    private val settingsModule = AndroidSettingsModule(
        context = application,
    )

    val settingsAccess = settingsModule.settingsAccess
    val appSettings: List<Setting<*>> = NotionSettings.all
}
