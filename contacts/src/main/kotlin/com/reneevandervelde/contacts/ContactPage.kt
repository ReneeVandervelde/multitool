package com.reneevandervelde.contacts

import com.reneevandervelde.notion.page.Page
import com.reneevandervelde.notion.property.Property
import com.reneevandervelde.notion.property.PropertyName
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format

@JvmInline
value class ContactPage(val page: Page)
{
    val notionUrl get() = page.url
    val id get() = (page.properties[Properties.Id] as Property.UniqueId)
        .let { "${it.prefix}-${it.number}" }
    val name get() = (page.properties[Properties.Name] as Property.Title)
        .toPlainText()
    val phone get() = (page.properties[Properties.Phone] as Property.PhoneNumber)
        .number
    val email get() = (page.properties[Properties.Email] as Property.Email)
        .email
    val workEmail get() = (page.properties[Properties.WorkEmail] as Property.Email)
        .email
    val address get() = (page.properties[Properties.Address] as Property.RichText)
        .toPlainText()
    val birthDate get() = (page.properties[Properties.BirthDate] as Property.Date)
        .toLocalDate()
        ?.let {
            if (it.year == 1900) "--${it.monthNumber.toString().padStart(2, '0')}-${it.dayOfMonth.toString().padStart(2, '0')}"
            else it.format(LocalDate.Formats.ISO)
        }

    object Properties
    {
        val Id = PropertyName("ID")
        val Name = PropertyName("Name")
        val Phone = PropertyName("Phone")
        val Email = PropertyName("Email")
        val WorkEmail = PropertyName("Work Email")
        val Address = PropertyName("Address")
        val BirthDate = PropertyName("Birthdate")
    }
}
