package com.reneevandervelde.notion

import com.reneevandervelde.notion.database.DatabaseId
import com.reneevandervelde.notion.database.DatabaseQuery
import com.reneevandervelde.notion.page.NewPage
import com.reneevandervelde.notion.page.Page
import com.reneevandervelde.notion.page.PageId
import com.reneevandervelde.notion.property.PropertyArgument
import com.reneevandervelde.notion.property.PropertyName
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

internal class NotionApiClient: NotionApi
{
    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val httpClient = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = 20.seconds.inWholeMilliseconds
        }
        install(ContentNegotiation) {
            json(json)
        }
    }
    override suspend fun queryDatabase(
        token: NotionBearerToken,
        database: DatabaseId,
        query: DatabaseQuery
    ): NotionResponse.ListResponse<Page> {
        return httpClient.post("https://api.notion.com/v1/databases/${database.value}/query") {
            notionHeaders(token)
            setBody(query)
        }.body()
    }

    override suspend fun createPage(token: NotionBearerToken, page: NewPage) {
        val response = httpClient.post("https://api.notion.com/v1/pages") {
            notionHeaders(token)
            setBody(page)
        }

        if (!response.status.isSuccess()) {
            throw IllegalStateException("Failed to create page: ${response.status}. ${response.bodyAsText()}")
        }
    }

    override suspend fun updatePage(
        token: NotionBearerToken,
        page: PageId,
        properties: Map<PropertyName, PropertyArgument>,
    ) {
        val response = httpClient.patch("https://api.notion.com/v1/pages/${page.value}") {
            notionHeaders(token)
            setBody(mapOf("properties" to properties))
        }

        if (!response.status.isSuccess()) {
            throw IllegalStateException("Failed to update page: ${response.status}. ${response.bodyAsText()}")
        }
    }

    override suspend fun archivePage(token: NotionBearerToken, page: PageId) {
        val response = httpClient.patch("https://api.notion.com/v1/pages/${page.value}") {
            notionHeaders(token)
            setBody(mapOf("archived" to true))
        }

        if (!response.status.isSuccess()) {
            throw IllegalStateException("Failed to archive page: ${response.status}. ${response.bodyAsText()}")
        }
    }

    private fun HttpMessageBuilder.notionHeaders(token: NotionBearerToken)
    {
        accept(ContentType.Application.Json)
        contentType(ContentType.Application.Json)
        header("Authorization", token.value)
        header("Notion-Version", "2022-06-28")
    }
}
