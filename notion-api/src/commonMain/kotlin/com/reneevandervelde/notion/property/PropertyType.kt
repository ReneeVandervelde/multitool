package com.reneevandervelde.notion.property

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class PropertyType(val value: String)
{
    companion object
    {
        val MultiSelect = PropertyType("multi_select")
        val Select = PropertyType("select")
        val Title = PropertyType("title")
        val RichText = PropertyType("rich_text")
        val Number = PropertyType("number")
        val PhoneNumber = PropertyType("phone_number")
        val Email = PropertyType("email")
        val Date = PropertyType("date")
        val UniqueId = PropertyType("unique_id")
        val Formula = PropertyType("formula")
    }
}
