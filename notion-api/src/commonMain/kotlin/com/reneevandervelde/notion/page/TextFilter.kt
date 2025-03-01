package com.reneevandervelde.notion.page

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = TextFilterSerializer::class)
sealed interface TextFilter
{
    data class Equals(
        val equals: String,
    ): TextFilter

    data class Contains(
        val contains: String,
    ): TextFilter

    data object IsNotEmpty: TextFilter
    data object IsEmpty: TextFilter
}

internal class TextFilterSerializer: KSerializer<TextFilter>
{
    override val descriptor: SerialDescriptor = Surrogate.serializer().descriptor

    override fun deserialize(decoder: Decoder): TextFilter = TODO()

    override fun serialize(encoder: Encoder, value: TextFilter)
    {
        val surrogate = when (value) {
            is TextFilter.Equals -> Surrogate(
                equals = value.equals,
            )
            is TextFilter.Contains -> Surrogate(
                contains = value.contains,
            )
            TextFilter.IsNotEmpty -> Surrogate(
                is_not_empty = true,
            )
            TextFilter.IsEmpty -> Surrogate(
                is_empty = true,
            )
        }
        Surrogate.serializer().serialize(encoder, surrogate)
    }

    @Serializable
    private data class Surrogate(
        val contains: String? = null,
        val equals: String? = null,
        val is_empty: Boolean? = null,
        val is_not_empty: Boolean? = null,
    )
}
