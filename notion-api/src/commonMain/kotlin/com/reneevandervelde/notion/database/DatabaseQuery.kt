package com.reneevandervelde.notion.database

import com.reneevandervelde.notion.page.PageFilter
import kotlinx.serialization.Serializable

@Serializable
data class DatabaseQuery(
    val filter: PageFilter? = null,
)
