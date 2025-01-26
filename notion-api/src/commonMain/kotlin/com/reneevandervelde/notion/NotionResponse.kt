package com.reneevandervelde.notion

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface NotionResponse
{
    @Serializable
    data class ListResponse<T>(
        val results: List<T> = emptyList(),
        @SerialName("has_more")
        val hasMore: Boolean = false,
        @SerialName("next_cursor")
        val nextCursor: PageCursor? = null,
    ): NotionResponse
}
