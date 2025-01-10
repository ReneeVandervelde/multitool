package com.reneevandervelde.notion.database

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class DatabaseId(val value: String)
