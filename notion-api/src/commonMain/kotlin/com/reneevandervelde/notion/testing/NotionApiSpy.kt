package com.reneevandervelde.notion.testing

import com.reneevandervelde.notion.NotionApi
import com.reneevandervelde.notion.NotionBearerToken
import com.reneevandervelde.notion.NotionResponse
import com.reneevandervelde.notion.database.DatabaseId
import com.reneevandervelde.notion.database.DatabaseQuery
import com.reneevandervelde.notion.page.NewPage
import com.reneevandervelde.notion.page.Page
import com.reneevandervelde.notion.page.PageId
import com.reneevandervelde.notion.property.PropertyArgument
import com.reneevandervelde.notion.property.PropertyName

open class NotionApiSpy: NotionApi
{
    val queries = mutableListOf<DatabaseQuery>()
    val getPages = mutableListOf<PageId>()
    val createdPages = mutableListOf<NewPage>()
    val archivedPages = mutableListOf<PageId>()
    val updatedPages = mutableListOf<Pair<PageId, Map<PropertyName, PropertyArgument>>>()

    override suspend fun queryDatabase(
        token: NotionBearerToken,
        database: DatabaseId,
        query: DatabaseQuery
    ): NotionResponse.ListResponse<Page> {
        queries.add(query)
        return NotionResponse.ListResponse(emptyList())
    }

    override suspend fun createPage(token: NotionBearerToken, page: NewPage)
    {
        createdPages.add(page)
    }

    override suspend fun getPage(token: NotionBearerToken, page: PageId): Page {
        getPages.add(page)
        TODO("Stub -- override and catch to use")
    }

    override suspend fun updatePage(
        token: NotionBearerToken,
        page: PageId,
        properties: Map<PropertyName, PropertyArgument>
    ) {
        updatedPages.add(page to properties)
    }

    override suspend fun archivePage(token: NotionBearerToken, page: PageId)
    {
        archivedPages.add(page)
    }
}
