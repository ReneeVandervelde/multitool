package com.reneevandervelde.notion.page

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = FilterQuerySerializer::class)
sealed interface ValueFilter
{
    data class Contains(
        val contains: String,
    ): ValueFilter

    data class Equals(
        val value: String,
    ): ValueFilter

    data class DoesNotEqual(
        val value: String,
    ): ValueFilter

    data object IsEmpty: ValueFilter

    data object IsNotEmpty: ValueFilter
}

internal class FilterQuerySerializer: KSerializer<ValueFilter>
{
    override val descriptor: SerialDescriptor = Surrogate.serializer().descriptor

    override fun deserialize(decoder: Decoder): ValueFilter = TODO("Not yet implemented")

    override fun serialize(encoder: Encoder, value: ValueFilter)
    {
        val surrogate = when (value) {
            is ValueFilter.Contains -> Surrogate(
                contains = value.contains,
            )
            is ValueFilter.Equals -> Surrogate(
                equals = value.value,
            )
            is ValueFilter.DoesNotEqual -> Surrogate(
                does_not_equal = value.value,
            )
            ValueFilter.IsEmpty -> Surrogate(
                is_empty = true,
            )
            ValueFilter.IsNotEmpty -> Surrogate(
                is_not_empty = true,
            )
        }
        Surrogate.serializer().serialize(encoder, surrogate)
    }

    @Serializable
    private data class Surrogate(
        val contains: String? = null,
        val equals: String? = null,
        val does_not_equal: String? = null,
        val is_empty: Boolean? = null,
        val is_not_empty: Boolean? = null,
    )
}
