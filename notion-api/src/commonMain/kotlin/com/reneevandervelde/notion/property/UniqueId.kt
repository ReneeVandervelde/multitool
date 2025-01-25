package com.reneevandervelde.notion.property

import kotlinx.serialization.Serializable

@Serializable
internal data class UniqueId(
    val number: Int,
    val prefix: String? = null,
)
