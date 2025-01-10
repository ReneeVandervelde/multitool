package com.reneevandervelde.notion

import com.reneevandervelde.notion.database.DatabaseId
import com.reneevandervelde.notion.database.DatabaseQuery
import com.reneevandervelde.notion.page.Page

interface NotionApi
{
    suspend fun queryDatabase(
        token: NotionBearerToken,
        database: DatabaseId,
        query: DatabaseQuery,
        ): NotionResponse.ListResponse<Page>

}
