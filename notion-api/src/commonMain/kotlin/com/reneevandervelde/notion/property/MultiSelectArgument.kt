package com.reneevandervelde.notion.property

import kotlinx.serialization.Serializable

@Serializable
data class MultiSelectArgument(
    val id: String? = null,
    val name: String? = null,
)
