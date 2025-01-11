package com.reneevandervelde.notion.property

import kotlinx.serialization.Serializable

@Serializable
data class MultiSelectOption(
    val id: String,
    val name: String,
)
