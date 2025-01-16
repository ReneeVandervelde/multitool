package com.reneevandervelde.radio.formats

import com.reneevandervelde.radio.ChannelPage
import com.reneevandervelde.radio.units.Mode
import com.reneevandervelde.radio.units.abs
import com.reneevandervelde.radio.units.hertz
import inkapplications.spondee.format.SimpleNumberFormats
import inkapplications.spondee.format.formatDecimal
import inkapplications.spondee.structure.Mega
import inkapplications.spondee.structure.compareTo
import inkapplications.spondee.structure.value

@OptIn(SimpleNumberFormats::class)
object ChirpFormat: ChannelExportFormat
{
    override fun fields(channels: List<ChannelPage>): List<Map<String, String>>
    {
        return channels.mapIndexed { index, channel ->
            val offset = channel.offset
            mapOf(
                "Location" to index.toString(),
                "Name" to channel.alias,
                "Frequency" to channel.frequency
                    ?.toHertz()
                    ?.value(Mega)
                    ?.formatDecimal(
                        decimals = 6,
                        round = false,
                    )
                    .orEmpty(),
                "Duplex" to when {
                    offset == null -> ""
                    offset.toHertz() >= 0.hertz -> "+"
                    else -> "-"
                },
                "Offset" to offset?.toHertz()
                    ?.let { abs(it) }
                    ?.value(Mega)
                    ?.let {
                        it.formatDecimal(
                            decimals = 6,
                            round = false,
                        )
                    }.orEmpty(),
                "Tone" to when {
                    channel.rxCtcss != null || channel.txCtcss != null -> "TSQL"
                    else -> ""
                },
                "rToneFreq" to channel.rxCtcss
                    ?.toHertz()
                    ?.value()
                    ?.formatDecimal(
                        decimals = 1,
                        round = false,
                    )
                    .orEmpty(),
                "cToneFreq" to channel.txCtcss
                    ?.toHertz()
                    ?.value()
                    ?.formatDecimal(
                        decimals = 1,
                        round = false,
                    ).orEmpty(),
                "DtcsCode" to "023",
                "DtcsPolarity" to "NN",
                "Mode" to when (channel.mode) {
                    Mode.FM -> "FM"
                    Mode.NFM -> "NFM"
                    else -> ""
                },
                "TStep" to "5",
                "Skip" to "",
                "Comment" to "",
                "URCALL" to "",
                "RPT1CALL" to "",
                "RPT2CALL" to "",
                "DVCODE" to "",
            )
        }
    }
}
