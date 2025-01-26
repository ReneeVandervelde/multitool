package com.reneevandervelde.notion.database

import com.reneevandervelde.notion.PageCursor
import com.reneevandervelde.notion.page.PageFilter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DatabaseQuery(
    val filter: PageFilter? = null,
    @SerialName("start_cursor")
    val startCursor: PageCursor? = null,
)
