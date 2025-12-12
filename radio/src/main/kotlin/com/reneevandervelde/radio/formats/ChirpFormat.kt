package com.reneevandervelde.radio.formats

import com.reneevandervelde.radio.ChannelPage
import com.reneevandervelde.radio.TalkgroupPage
import com.reneevandervelde.radio.units.*
import com.reneevandervelde.radio.units.TransmitPower.Companion.High
import com.reneevandervelde.radio.units.TransmitPower.Companion.Low
import com.reneevandervelde.radio.units.TransmitPower.Companion.Max
import com.reneevandervelde.radio.units.TransmitPower.Companion.Medium
import com.reneevandervelde.radio.units.TransmitPower.Companion.Off
import inkapplications.spondee.format.SimpleNumberFormats
import inkapplications.spondee.format.formatDecimal
import inkapplications.spondee.structure.Mega
import inkapplications.spondee.structure.compareTo
import inkapplications.spondee.structure.value

enum class ChirpVariant {
    Uv5r,
    BfF8hp,
    TDH3,
}
@OptIn(SimpleNumberFormats::class)
class ChirpFormat(
    private val variant: ChirpVariant,
): RadioExportFormat {
    override fun fields(
        channels: List<ChannelPage>,
        talkgroups: Set<TalkgroupPage>
    ): TableData {
        return channels
            .filter { it.encoding == Encoding.Analog }
            .filter { it.mode in setOf(Mode.FM, Mode.NFM) }
            .mapIndexed { index, channel ->
                val offset = channel.offset
                mapOf(
                    "Location" to index.toString(),
                    "Name" to channel.alias.orEmpty(),
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
                        ?.formatDecimal(
                            decimals = 6,
                            round = false,
                        )
                        .let { it ?: "0" },
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
                        .let { it ?: "88.5" },
                    "cToneFreq" to channel.txCtcss
                        ?.toHertz()
                        ?.value()
                        ?.formatDecimal(
                            decimals = 1,
                            round = false,
                        )
                        .let { it ?: "88.5" },
                    "DtcsCode" to "023",
                    "DtcsPolarity" to "NN",
                    "RxDtcsCode" to "023",
                    "CrossMode" to "Tone->Tone",
                    "Mode" to when (channel.mode) {
                        Mode.FM -> "FM"
                        Mode.NFM -> "NFM"
                        else -> ""
                    },
                    "TStep" to "5",
                    "Skip" to if (channel.excludeScanning) "S" else "",
                    "Power" to when (variant) {
                        ChirpVariant.Uv5r -> when (channel.transmit) {
                            Max, High, Medium -> "4.0W"
                            else -> "1.0W"
                        }
                        ChirpVariant.TDH3 -> when (channel.transmit) {
                            Max, High, Medium -> "5.0W"
                            else -> "2.0W"
                        }
                        ChirpVariant.BfF8hp -> when (channel.transmit) {
                            Max, High -> "8.0W"
                            Medium -> "4.0W"
                            else -> "1.0W"
                        }
                    },
                    "Comment" to "",
                    "URCALL" to "",
                    "RPT1CALL" to "",
                    "RPT2CALL" to "",
                    "DVCODE" to "",
                )
            }.let(::TableData)
    }
}
