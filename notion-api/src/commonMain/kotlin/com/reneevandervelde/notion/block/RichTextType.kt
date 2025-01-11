package com.reneevandervelde.notion.block

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class RichTextType(val value: String)
{
    companion object
    {
        val Text = RichTextType("text")
    }
}
