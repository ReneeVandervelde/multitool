package com.reneevandervelde.radio

import com.reneevandervelde.notion.page.Page
import com.reneevandervelde.notion.property.Property
import com.reneevandervelde.notion.property.PropertyName

const val ALL_CALL = 16777215

@JvmInline
value class TalkgroupPage(val page: Page)
{
    val talkgroupId get() = page.getProperty<Property.Number>(Properties.Id)
        .number
        ?.toInt()

    val name get() = page.getProperty<Property.Title>(Properties.Name)
        .toPlainText()

    val alerts get() = page.getProperty<Property.Checkbox>(Properties.Alerts)
        .value

    val private get() = page.getProperty<Property.Checkbox>(Properties.Private)
        .value

    object Properties
    {
        val Id = PropertyName("ID")
        val Name = PropertyName("Name")
        val Alerts = PropertyName("Alerts")
        val Private = PropertyName("Private")
    }
}
