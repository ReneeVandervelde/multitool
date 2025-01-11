package com.reneevandervelde.radio

import com.reneevandervelde.notion.NotionModule
import com.reneevandervelde.notion.block.RichText
import com.reneevandervelde.notion.database.DatabaseQuery
import com.reneevandervelde.notion.property.Property
import com.reneevandervelde.notion.property.PropertyName
import com.reneevandervelde.radio.settings.radioNotionSettings
import com.reneevandervelde.settings.SettingsModule
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
        result.results.forEach {
            val name = it.properties[PropertyName("Name")]
                .let { it as Property.Title }
                .title
                .map { it as RichText.Text }
                .joinToString { it.plain_text }
            val frequency = it.properties[PropertyName("Rx Freq")]
                .let { it as Property.Number? }
                ?.number
            println("$name @ $frequency")
        }
    }
}
