package com.reneevandervelde.tasks

import com.reneevandervelde.notion.page.Page
import com.reneevandervelde.notion.property.Property
import com.reneevandervelde.notion.property.PropertyName

@JvmInline
value class TaskPage(val page: Page)
{
    val title get() = page.getProperty<Property.Title>(Properties.Name)
        .toPlainText()

    val dueDate get() = page.getProperty<Property.Date>(Properties.DueDate)
        .toLocalDate()

    val timeframe get() = page.getProperty<Property.TextFormula>(Properties.Timeframe)
        .value

    val impact get() = page.getProperty<Property.Select>(Properties.Impact)
        .select?.name?.let { LevelChoice.valueOf(it) }

    val urgency get() = page.getProperty<Property.Select>(Properties.Urgency)
        .select?.name?.let { LevelChoice.valueOf(it) }

    object Properties
    {
        val Timeframe = PropertyName("Timeframe")
        val Impact = PropertyName("Impact")
        val Urgency = PropertyName("Urgency")
        val Name = PropertyName("Name")
        val Complete = PropertyName("Complete")
        val DueDate = PropertyName("Due Date")
    }
}
