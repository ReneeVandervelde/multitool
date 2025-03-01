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
object CpsZoneFormat: RadioExportFormat
{
    override fun fields(
        channels: List<ChannelPage>,
        talkgroups: Set<TalkgroupPage>
    ): TableData {
        val zones = channels
            .flatMap { it.tags }
            .distinct()
        return zones.mapIndexed { index, zone ->
            val zoneChannels = channels
                .filter { zone in it.tags }
            mapOf(
                "No." to index.plus(1).toString(),
                "Zone Name" to zone,
                "Zone Channel Member" to zoneChannels
                    .map { it.name }
                    .joinToString("|"),
                "Zone Channel Member RX Frequency" to zoneChannels
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
                "Zone Channel Member TX Frequency" to zoneChannels
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
                "A Channel" to zoneChannels.first().name,
                "A Channel RX Frequency" to zoneChannels.first()
                    .frequency
                    !!.toHertz()
                    .value(Mega)
                    .formatDecimal(
                        decimals = 5,
                        round = false,
                    ),
                "A Channel TX Frequency" to zoneChannels.first()
                    .frequency
                    !!.toHertz()
                    .let { it.toHertz() + (zoneChannels.first().offset?.toHertz() ?: 0.hertz) }
                    .value(Mega)
                    .formatDecimal(
                        decimals = 5,
                        round = false,
                    ),
                "B Channel" to zoneChannels.getOrNull(1)?.name.orEmpty(),
                "B Channel RX Frequency" to zoneChannels.getOrNull(1)
                    .let { it ?: zoneChannels.first() }
                    .frequency
                    !!.toHertz()
                    .value(Mega)
                    .formatDecimal(
                        decimals = 5,
                        round = false,
                    ),
                "B Channel TX Frequency" to zoneChannels.getOrNull(1)
                    .let { it ?: zoneChannels.first() }
                    .frequency
                    !!.toHertz()
                    .let { it.toHertz() + (zoneChannels.getOrNull(1)?.offset?.toHertz() ?: 0.hertz) }
                    .value(Mega)
                    .formatDecimal(
                        decimals = 5,
                        round = false,
                    ),
                "Zone Hide" to "0",
            )
        }.let(::TableData)
    }
}
