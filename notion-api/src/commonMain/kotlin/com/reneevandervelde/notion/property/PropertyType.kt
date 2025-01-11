package com.reneevandervelde.notion.property

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class PropertyType(val value: String)
{
    companion object
    {
        val MultiSelect = PropertyType("multi_select")
        val Title = PropertyType("title")
        val RichText = PropertyType("rich_text")
        val Nunber = PropertyType("number")
    }
}
