package com.reneevandervelde.radio.formats

import com.reneevandervelde.radio.ALL_CALL
import com.reneevandervelde.radio.ChannelPage
import com.reneevandervelde.radio.TalkgroupPage

object CpsTalkgroupFormat: RadioExportFormat
{
    override fun fields(
        channels: List<ChannelPage>,
        talkgroups: Set<TalkgroupPage>
    ): TableData {
        return talkgroups
            .filter { it.talkgroupId != ALL_CALL } // All Call blocked from import
            .mapIndexed { index, talkgroup ->
                mapOf(
                    "No." to index.plus(1).toString(),
                    "Radio ID" to talkgroup.talkgroupId?.toString().orEmpty(),
                    "Name" to talkgroup.name,
                    "Call Type" to when {
                        talkgroup.talkgroupId == ALL_CALL -> "All Call"
                        talkgroup.private -> "Private Call"
                        else -> "Group Call"
                    },
                    "Call Alert" to when {
                        !talkgroup.alerts -> "None"
                        !talkgroup.private -> "Online Alert"
                        else -> "Ring"
                    },
                )
            }.let(::TableData)
    }
}

