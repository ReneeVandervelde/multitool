package com.reneevandervelde.radio

import com.reneevandervelde.notion.page.Page
import com.reneevandervelde.notion.property.Property
import com.reneevandervelde.notion.property.PropertyName
import com.reneevandervelde.radio.units.Mode
import com.reneevandervelde.radio.units.TransmitPower
import com.reneevandervelde.radio.units.hertz
import com.reneevandervelde.radio.units.megahertz

@JvmInline
value class ChannelPage(
    val page: Page,
) {
    val name get() = page.getProperty<Property.Title>(Properties.Name)
        .toPlainText()

    val alias get() = page.getProperty<Property.RichText>(Properties.Alias)
        .toPlainText()

    val frequency get() = page.getProperty<Property.Number>(Properties.RxFrequency)
        .number
        ?.megahertz

    val offset get()= page.getProperty<Property.Number>(Properties.Offset)
        .number
        ?.megahertz

    val rxCtcss get() = page.getProperty<Property.Number>(Properties.RxCtcss)
        .number
        ?.hertz

    val txCtcss get() = page.getProperty<Property.Number>(Properties.TxCtcss)
        .number
        ?.hertz

    val transmit get() = page.getProperty<Property.Select>(Properties.Transmit)
        .select
        ?.name
        ?.let(::TransmitPower)

    val mode get() = page.getProperty<Property.Select>(Properties.Mode)
        .select
        ?.name
        ?.let(::Mode)

    object Properties
    {
        val Name = PropertyName("Name")
        val RxFrequency = PropertyName("Rx Freq")
        val Alias = PropertyName("Alias")
        val Offset = PropertyName("Offset")
        val RxCtcss = PropertyName("Rx CTCSS")
        val TxCtcss = PropertyName("Tx CTCSS")
        val Transmit = PropertyName("Transmit")
        val Mode = PropertyName("Mode")
    }
}
