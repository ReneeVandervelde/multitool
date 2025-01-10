package com.reneevandervelde.notion

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class ParentType(val value: String)
{
    companion object
    {
        val DatabaseId = ParentType("database_id")
    }
}
