package com.reneevandervelde.radio.formats

import com.reneevandervelde.radio.ChannelPage

interface ChannelExportFormat
{
    fun fields(channels: List<ChannelPage>): List<Map<String, String>>
}
