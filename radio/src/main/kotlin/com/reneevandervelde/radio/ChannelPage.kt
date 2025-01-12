package com.reneevandervelde.radio

import com.reneevandervelde.notion.page.Page
import com.reneevandervelde.notion.property.Property
import com.reneevandervelde.notion.property.PropertyName

@JvmInline
value class ChannelPage(
    val page: Page,
) {
    val name get() = page.getProperty<Property.Title>(Properties.Name)
        .toPlainText()

    val frequency get() = page.getProperty<Property.Number>(Properties.RxFrequency)
        .number

    object Properties
    {
        val Name = PropertyName("Name")
        val RxFrequency = PropertyName("Rx Freq")
    }
}
