package com.reneevandervelde.notion.block

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class BlockType(val value: String)
{
    companion object
    {
        val PARAGRAPH = BlockType("paragraph")
        val CODE = BlockType("code")
    }
}
