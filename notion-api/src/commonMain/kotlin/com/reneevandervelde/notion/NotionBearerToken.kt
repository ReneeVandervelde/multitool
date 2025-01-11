package com.reneevandervelde.notion

@JvmInline
value class NotionBearerToken(val value: String)
{
    override fun toString(): String
    {
        return "[Notion Bearer Token]"
    }
}
