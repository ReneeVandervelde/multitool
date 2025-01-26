package com.reneevandervelde.radio.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.reneevandervelde.notion.NotionModule
import com.reneevandervelde.notion.database.DatabaseQuery
import com.reneevandervelde.notion.page.CheckboxFilter
import com.reneevandervelde.notion.page.PageFilter
import com.reneevandervelde.notion.page.ValueFilter
import com.reneevandervelde.notion.queryDatabaseForAll
import com.reneevandervelde.radio.ChannelPage
import com.reneevandervelde.radio.formats.ChirpFormat
import com.reneevandervelde.radio.settings.radioNotionSettings
import com.reneevandervelde.settings.SettingsModule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object ChannelExportCommand: CliktCommand(
    name = "export"
) {
    val format by option().choice(
        "chirp",
    ).default("chirp")

    val tags by option("--tag").multiple()

    override fun run()
    {
        runBlocking {
            val settings = SettingsModule().settingsAccess.radioNotionSettings.first()
                ?: run {
                    println("Radio settings not configured.")
                    return@runBlocking
                }

            val results = NotionModule().client.queryDatabaseForAll(
                token = settings.apiToken,
                database = settings.databaseId,
                query = DatabaseQuery(
                    filter = PageFilter.And(
                        listOfNotNull(
                            PageFilter.CheckboxFormula(
                                property = ChannelPage.Properties.Valid,
                                filter = CheckboxFilter.Equals(true),
                            ),
                            PageFilter.Or(
                                *tags.map { tag ->
                                    PageFilter.MultiSelect(
                                        property = ChannelPage.Properties.Tags,
                                        filter = ValueFilter.Contains(tag)
                                    )
                                }.toTypedArray()
                            ).takeIf { tags.isNotEmpty() }
                        )
                    ),
                )
            )

            val csvFields = ChirpFormat.fields(
                channels = results.map { ChannelPage(it) }
            )

            val headerRow = csvFields.flatMap { it.keys }
                .distinct()
                .joinToString(",")
            val valueRows = csvFields.map {
                it.values.joinToString(",")
            }

            println(headerRow)
            valueRows.forEach(::println)
        }
    }
}
