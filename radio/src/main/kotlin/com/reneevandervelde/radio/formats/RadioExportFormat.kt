package com.reneevandervelde.radio.formats

import com.reneevandervelde.radio.ChannelPage
import com.reneevandervelde.radio.TalkgroupPage

interface RadioExportFormat
{
    fun fields(
        channels: List<ChannelPage>,
        talkgroups: Set<TalkgroupPage>,
    ): TableData
}
