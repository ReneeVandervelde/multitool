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
import com.reneevandervelde.radio.TalkgroupPage
import com.reneevandervelde.radio.formats.*
import com.reneevandervelde.radio.settings.radioNotionSettings
import com.reneevandervelde.settings.SettingsModule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object ChannelExportCommand: CliktCommand(
    name = "export"
) {
    val format by option().choice(
        "chirp-uv5r",
        "chirp-bff8hp",
        "chirp-tdh3",
        "cps-channels",
        "cps-zones",
        "cps-talkgroups",
        "cps-scans",
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
            val notion = NotionModule().client

            val channelResults = notion.queryDatabaseForAll(
                token = settings.apiToken,
                database = settings.channelDatabaseId,
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
            val talkgroupResults = notion.queryDatabaseForAll(
                token = settings.apiToken,
                database = settings.talkgroupDatabaseId,
                query = DatabaseQuery()
            )

            val formatter: RadioExportFormat = when (format) {
                "chirp-uv5r" -> ChirpFormat(ChirpVariant.Uv5r)
                "chirp-bff8hp" -> ChirpFormat(ChirpVariant.BfF8hp)
                "chirp-tdh3" -> ChirpFormat(ChirpVariant.TDH3)
                "cps-channels" -> CpsChannelFormat
                "cps-zones" -> CpsZoneFormat
                "cps-talkgroups" -> CpsTalkgroupFormat
                "cps-scans" -> CpsScanFormat
                else -> error("Unknown format: $format")
            }
            val csvFields = formatter.fields(
                channels = channelResults.map { ChannelPage(it) },
                talkgroups = talkgroupResults.map { TalkgroupPage(it) }.toSet(),
            )

            val headerRow = csvFields.rows.flatMap { it.keys }
                .distinct()
                .joinToString(",")
            val valueRows = csvFields.rows.map {
                it.values.joinToString(",")
            }

            println(headerRow)
            valueRows.forEach(::println)
        }
    }
}
