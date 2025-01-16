package com.reneevandervelde.notion.property

import kotlinx.serialization.Serializable

@Serializable
data class SelectOption(
    val id: String,
    val name: String,
)
