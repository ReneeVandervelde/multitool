package com.reneevandervelde.radio

import com.reneevandervelde.notion.page.Page
import com.reneevandervelde.notion.property.Property
import com.reneevandervelde.notion.property.PropertyName
import com.reneevandervelde.radio.units.*

@JvmInline
value class ChannelPage(
    val page: Page,
) {
    val name get() = page.getProperty<Property.Title>(Properties.Name)
        .toPlainText()

    val alias get() = page.getProperty<Property.RichText>(Properties.Alias)
        .toPlainText()

    val frequency: Frequency? get() = page.getProperty<Property.Number>(Properties.RxFrequency)
        .number
        ?.megahertz

    val offset: Frequency? get() = page.getProperty<Property.Number>(Properties.Offset)
        .number
        ?.megahertz

    val rxCtcss: Frequency? get() = page.getProperty<Property.Number>(Properties.RxCtcss)
        .number
        ?.hertz

    val txCtcss: Frequency? get() = page.getProperty<Property.Number>(Properties.TxCtcss)
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

    val valid get() = page.getProperty<Property.BooleanFormula>(Properties.Valid)
        .value

    val tags get() = page.getProperty<Property.MultiSelect>(Properties.Tags)
        .multi_select
        .map { it.name }

    val encoding get() = page.getProperty<Property.Select>(Properties.Encoding)
        .select
        ?.name
        ?.let(::Encoding)

    val talkgroups get() = page.getProperty<Property.Relation>(Properties.Talkgroups)
        .relation
        .singleOrNull()

    val busyLock get() = page.getProperty<Property.Checkbox>(Properties.BusyLock)
        .value

    val colorCode get() = page.getProperty<Property.Select>(Properties.ColorCode)
        .select
        ?.name
        ?.let(::ColorCode)

    val aprsTransmit get() = page.getProperty<Property.Checkbox>(Properties.AprsTransmit)
        .value

    val excludeScanning get() = page.getProperty<Property.Checkbox>(Properties.ExcludeScanning)
        .value

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
        val Valid = PropertyName("Valid")
        val Tags = PropertyName("Tags")
        val Encoding = PropertyName("Encoding")
        val Talkgroups = PropertyName("DMR Talkgroups")
        val BusyLock = PropertyName("Busy Lock")
        val ColorCode = PropertyName("DMR: Color Code")
        val AprsTransmit = PropertyName("APRS Transmit")
        val ExcludeScanning = PropertyName("Exclude Scanning")
    }
}
