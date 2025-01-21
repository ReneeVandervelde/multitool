package com.reneevandervelde.contacts.settings

import com.inkapplications.data.transformer.DataTransformer
import com.reneevandervelde.notion.database.DatabaseId

object NotionDatabaseIdTransformer: DataTransformer<String?, DatabaseId?>
{
    override fun transform(data: String?): DatabaseId?
    {
        return data?.let(::DatabaseId)
    }

    override fun reverseTransform(data: DatabaseId?): String?
    {
        return data?.value
    }
}
