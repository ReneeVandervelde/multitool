package com.reneevandervelde.notion

import com.reneevandervelde.notion.database.DatabaseId
import com.reneevandervelde.notion.database.DatabaseQuery
import com.reneevandervelde.notion.page.NewPage
import com.reneevandervelde.notion.page.Page
import com.reneevandervelde.notion.page.PageId
import com.reneevandervelde.notion.property.PropertyArgument
import com.reneevandervelde.notion.property.PropertyName

interface NotionApi
{
    suspend fun queryDatabase(
        token: NotionBearerToken,
        database: DatabaseId,
        query: DatabaseQuery,
    ): NotionResponse.ListResponse<Page>

    suspend fun createPage(
        token: NotionBearerToken,
        page: NewPage,
    )

    suspend fun getPage(
        token: NotionBearerToken,
        page: PageId,
    ): Page

    suspend fun updatePage(
        token: NotionBearerToken,
        page: PageId,
        properties: Map<PropertyName, PropertyArgument>
    )

    suspend fun archivePage(
        token: NotionBearerToken,
        page: PageId,
    )
}

private suspend fun NotionApi.accumulatePages(
    token: NotionBearerToken,
    database: DatabaseId,
    query: DatabaseQuery,
    accumulatedPages: List<Page> = emptyList(),
): List<Page> {
    val response = queryDatabase(
        token = token,
        database = database,
        query = query
    )
    val newAccumulatedPages = accumulatedPages + response.results
    return if (response.nextCursor == null) {
        newAccumulatedPages
    } else {
        accumulatePages(
            token = token,
            database = database,
            query = query.copy(startCursor = response.nextCursor),
            accumulatedPages = newAccumulatedPages,
        )
    }
}

suspend fun NotionApi.queryDatabaseForAll(
    token: NotionBearerToken,
    database: DatabaseId,
    query: DatabaseQuery,
): List<Page> {
    return accumulatePages(
        token = token,
        database = database,
        query = query,
    )
}
