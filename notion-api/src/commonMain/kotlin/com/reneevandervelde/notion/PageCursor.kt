package com.reneevandervelde.notion

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class PageCursor(val value: String)
