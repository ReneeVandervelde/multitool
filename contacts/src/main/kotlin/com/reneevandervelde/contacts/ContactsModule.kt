package com.reneevandervelde.contacts

import android.app.Application
import com.reneevandervelde.contacts.settings.NotionSettings
import com.reneevandervelde.notion.NotionModule
import regolith.data.settings.AndroidSettingsModule
import regolith.data.settings.structure.Setting
import regolith.init.InitRunner
import regolith.init.RegolithInitRunner
import regolith.processes.daemon.DaemonInitializer

class ContactsModule(
    application: Application,
) {
    private val settingsModule = AndroidSettingsModule(
        context = application,
    )

    val settingsAccess = settingsModule.settingsAccess
    val appSettings: List<Setting<*>> = NotionSettings.all
    private val daemonInit = DaemonInitializer(
        daemons = listOf(
            ContactSync(
                application.contentResolver,
                notion = NotionModule().client,
                settingsAccess = settingsAccess,
            )
        )
    )
    val initRunner: InitRunner = RegolithInitRunner(
        initializers = listOf(
            daemonInit,
        )
    )
}
