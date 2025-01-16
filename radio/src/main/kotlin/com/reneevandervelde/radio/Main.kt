package com.reneevandervelde.radio

import com.reneevandervelde.notion.NotionModule
import com.reneevandervelde.notion.database.DatabaseQuery
import com.reneevandervelde.radio.settings.radioNotionSettings
import com.reneevandervelde.settings.SettingsModule
import inkapplications.spondee.structure.Mega
import inkapplications.spondee.structure.format
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>)
{
    runBlocking {
        val settings = SettingsModule().settingsAccess.radioNotionSettings.first()
            ?: return@runBlocking

        val result = NotionModule().client.queryDatabase(
            token = settings.apiToken,
            database = settings.databaseId,
            query = DatabaseQuery()
        )
        result.results
            .map { ChannelPage(it) }
            .forEach {
                println("${it.name} -> ${it.mode} ${it.transmit} @ ${it.frequency?.format(Mega, decimals = 3)}")
            }
    }
}
