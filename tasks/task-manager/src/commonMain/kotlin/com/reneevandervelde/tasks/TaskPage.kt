package com.reneevandervelde.tasks

import com.reneevandervelde.notion.page.Page
import com.reneevandervelde.notion.property.Property
import com.reneevandervelde.notion.property.PropertyName

@JvmInline
value class TaskPage(val page: Page)
{
    val title get() = page.getProperty<Property.Title>(Properties.Name)
        .toPlainText()

    object Properties
    {
        val Name = PropertyName("Name")
        val Complete = PropertyName("Complete")
    }
}
