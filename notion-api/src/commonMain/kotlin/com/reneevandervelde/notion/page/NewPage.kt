package com.reneevandervelde.notion.page

import com.reneevandervelde.notion.Parent
import com.reneevandervelde.notion.block.BlockArgument
import com.reneevandervelde.notion.property.PropertyArgument
import com.reneevandervelde.notion.property.PropertyName
import kotlinx.serialization.Serializable

@Serializable
data class NewPage(
    val parent: Parent,
    val icon: PageIcon? = null,
    val properties: Map<PropertyName, PropertyArgument>,
    val children: List<BlockArgument>? = null,
)
