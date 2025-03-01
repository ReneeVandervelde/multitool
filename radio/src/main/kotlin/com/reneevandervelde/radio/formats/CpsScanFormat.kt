package com.reneevandervelde.radio.formats

import com.reneevandervelde.radio.ChannelPage
import com.reneevandervelde.radio.TalkgroupPage
import com.reneevandervelde.radio.units.hertz
import inkapplications.spondee.format.SimpleNumberFormats
import inkapplications.spondee.format.formatDecimal
import inkapplications.spondee.structure.Mega
import inkapplications.spondee.structure.plus
import inkapplications.spondee.structure.value

@OptIn(SimpleNumberFormats::class)
object CpsScanFormat: RadioExportFormat
{
    override fun fields(
        channels: List<ChannelPage>,
        talkgroups: Set<TalkgroupPage>
    ): TableData {
        val zones = channels
            .flatMap { it.tags }
            .distinct()
            .filter { zone -> channels.filter { zone in it.tags }.size > 1 }
        return zones.mapIndexed { index, zone ->
            val zoneChannels = channels
                .filter { zone in it.tags }
            mapOf(
                "No." to index.plus(1).toString(),
                "Scan List Name" to zone,
                "Scan Channel Member" to zoneChannels
                    .map { it.name }
                    .joinToString("|"),
                "Scan Channel Member RX Frequency" to zoneChannels
                    .map {
                        it.frequency
                            ?.toHertz()
                            ?.value(Mega)
                            ?.formatDecimal(
                                decimals = 5,
                                round = false,
                            )
                            .orEmpty()
                    }
                    .joinToString("|"),
                "Scan Channel Member TX Frequency" to zoneChannels
                    .map { channel ->
                        channel.frequency
                            ?.toHertz()
                            ?.let { it.toHertz() + (channel.offset?.toHertz() ?: 0.hertz) }
                            ?.value(Mega)
                            ?.formatDecimal(
                                decimals = 5,
                                round = false,
                            )
                            .orEmpty()
                    }
                    .joinToString("|"),
                "Scan Mode" to "Off",
                "Priority Channel Select" to "Off",
                "Priority Channel 1" to "Off",
                "Priority Channel 1 RX Frequency" to "",
                "Priority Channel 1 TX Frequency" to "",
                "Priority Channel 2" to "Off",
                "Priority Channel 2 RX Frequency" to "",
                "Priority Channel 2 TX Frequency" to "",
                "Revert Channel" to "Selected",
                "Look Back Time A[s]" to "0.1",
                "Look Back Time B[s]" to "0.1",
                "Dropout Delay Time[s]" to "5",
                "Dwell Time[s]" to "0.1"
            )
        }.let(::TableData)
    }
}
