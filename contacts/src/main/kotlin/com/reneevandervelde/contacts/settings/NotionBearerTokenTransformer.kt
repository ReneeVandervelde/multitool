package com.reneevandervelde.contacts.settings

import com.inkapplications.data.transformer.DataTransformer
import com.reneevandervelde.notion.NotionBearerToken

object NotionBearerTokenTransformer: DataTransformer<String?, NotionBearerToken?>
{
    override fun transform(data: String?): NotionBearerToken?
    {
        return data?.let(::NotionBearerToken)
    }

    override fun reverseTransform(data: NotionBearerToken?): String?
    {
        return data?.value
    }
}
