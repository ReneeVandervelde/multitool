package com.reneevandervelde.radio.formats

import com.reneevandervelde.radio.ChannelPage
import com.reneevandervelde.radio.TalkgroupPage
import com.reneevandervelde.radio.units.Encoding
import com.reneevandervelde.radio.units.Mode
import com.reneevandervelde.radio.units.TransmitPower
import com.reneevandervelde.radio.units.hertz
import inkapplications.spondee.format.SimpleNumberFormats
import inkapplications.spondee.format.formatDecimal
import inkapplications.spondee.structure.Mega
import inkapplications.spondee.structure.plus
import inkapplications.spondee.structure.value
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(SimpleNumberFormats::class)
object CpsChannelFormat: RadioExportFormat {
    private val talkgroups = MutableStateFlow<Set<TalkgroupPage>>(emptySet())
    override fun fields(
        channels: List<ChannelPage>,
        talkgroups: Set<TalkgroupPage>
    ): TableData {
        return channels
            .filter { it.encoding in setOf(Encoding.Analog, Encoding.DmrRepeater, Encoding.DmrSimplex) }
            .filter { it.mode in setOf(Mode.FM, Mode.NFM) }
            .mapIndexed { index, channel ->
                val name = channel.name
                    .takeIf { it.length <= 16 }
                    ?: channel.alias.orEmpty()

                mapOf(
                    "No." to index.plus(1).toString(),
                    "Channel Name" to name,
                    "Receive Frequency" to channel.frequency
                        ?.toHertz()
                        ?.value(Mega)
                        ?.formatDecimal(
                            decimals = 5,
                            round = false,
                        )
                        .orEmpty(),
                    "Transmit Frequency" to channel.frequency
                        ?.toHertz()
                        ?.let { it.toHertz() + (channel.offset?.toHertz() ?: 0.hertz) }
                        ?.value(Mega)
                        ?.formatDecimal(
                            decimals = 5,
                            round = false,
                        )
                        .orEmpty(),
                    "Channel Type" to when (channel.encoding) {
                        Encoding.Analog -> "A-Analog"
                        Encoding.DmrRepeater, Encoding.DmrSimplex -> "D-Digital"
                        else -> TODO("Not Implemented")
                    },
                    "Transmit Power" to when (channel.transmit) {
                        TransmitPower.Off -> "Low"
                        TransmitPower.Low -> "Low"
                        TransmitPower.Medium -> "Mid"
                        TransmitPower.High -> "High"
                        TransmitPower.Max -> "Turbo"
                        else -> "Low"
                    },
                    "Band Width" to when (channel.mode) {
                        Mode.FM -> "25K"
                        Mode.NFM -> "12.5K"
                        else -> TODO("Not Implemented")
                    },
                    "CTCSS/DCS Decode" to channel.rxCtcss
                        ?.toHertz()
                        ?.value()
                        ?.formatDecimal(
                            decimals = 1,
                            round = false,
                        )
                        .let { it ?: "Off" },
                    "CTCSS/DCS Encode" to channel.txCtcss
                        ?.toHertz()
                        ?.value()
                        ?.formatDecimal(
                            decimals = 1,
                            round = false,
                        )
                        .let { it ?: "Off" },
                    "Contact" to talkgroups
                        .firstOrNull { it.page.id == channel.talkgroups }
                        .let { it ?: talkgroups.minBy { it.talkgroupId ?: Int.MAX_VALUE } }
                        .name,
                    "Contact Call Type" to "Group Call",
                    "Contact TG/DMR ID" to "-1",
                    "Radio ID" to "KE0YOG",
                    "Busy Lock/TX Permit" to if(channel.busyLock) "ChannelFree" else "Always",
                    "Squelch Mode" to when {
                        channel.rxCtcss != null -> "CTCSS/DCS"
                        else -> "Carrier"
                    },
                    "Optional Signal" to "Off",
                    "DTMF ID" to "1",
                    "2Tone ID" to "1",
                    "5Tone ID" to "1",
                    "PTT ID" to "Off",
                    "RX Color Code" to (
                        channel.colorCode
                        ?.integer
                        ?.toString()
                        ?: "0"
                    ),
                    "Slot" to "1",
                    "Scan List" to "None",
                    "Receive Group List" to "None",
                    "PTT Prohibit" to when (channel.transmit) {
                        TransmitPower.Off -> "On"
                        else -> "Off"
                    },
                    "Reverse" to "Off",
                    "Simplex TDMA" to "Off",
                    "Slot Suit" to "Off",
                    "AES Digital Encryption" to "Normal Encryption",
                    "Digital Encryption" to "Off",
                    "Call Confirmation" to "Off",
                    "Talk Around(Simplex)" to "Off",
                    "Work Alone" to "Off",
                    "Custom CTCSS" to "251.1",
                    "2TONE Decode" to "0",
                    "Ranging" to "Off",
                    "Through Mode" to when (channel.encoding) {
                        Encoding.DmrSimplex -> "On"
                        else -> "Off"
                    },
                    "APRS RX" to "Off",
                    "Analog APRS PTT Mode" to "Off",
                    "Digital APRS PTT Mode" to "Off",
                    "APRS Report Type" to if (channel.aprsTransmit) "Analog" else "Off",
                    "Digital APRS Report Channel" to "1",
                    "Correct Frequency[Hz]" to "0",
                    "SMS Confirmation" to "Off",
                    "Exclude channel from roaming" to "0",
                    "DMR MODE" to "0",
                    "DataACK Disable" to "0",
                    "R5toneBot" to "0",
                    "R5ToneEot" to "0",
                    "Auto Scan" to "0",
                    "Ana Aprs Mute" to "0",
                    "Send Talker Alias" to "0",
                    "AnaAprsTxPath" to "0",
                    "ARC4" to "0",
                    "ex_emg_kind" to "0",
                    "TxCC" to "1",
                )
            }.let(::TableData)
    }
}
