package com.reneevandervelde.notion

import kotlinx.serialization.Serializable

sealed interface NotionResponse
{
    @Serializable
    data class ListResponse<T>(
        val results: List<T> = emptyList(),
    ): NotionResponse
}
