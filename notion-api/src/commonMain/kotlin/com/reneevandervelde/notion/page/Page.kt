package com.reneevandervelde.notion.page

import com.reneevandervelde.notion.Parent
import com.reneevandervelde.notion.property.Property
import com.reneevandervelde.notion.property.PropertyName
import kotlinx.serialization.Serializable

@Serializable
data class Page(
    val id: PageId,
    val parent: Parent,
    val properties: Map<PropertyName, Property>,
    val icon: PageIcon? = null,
    val url: String,
) {
    inline fun <reified T: Property> getProperty(name: PropertyName): T {
        return properties[name] as T
    }
}
