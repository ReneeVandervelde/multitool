package com.reneevandervelde.notion.page

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = FilterQuerySerializer::class)
sealed interface FilterQuery
{
    data class Contains(
        val contains: String,
    ): FilterQuery

    data class DoesNotEqual(
        val value: String,
    ): FilterQuery
}

internal class FilterQuerySerializer: KSerializer<FilterQuery>
{
    override val descriptor: SerialDescriptor = Surrogate.serializer().descriptor

    override fun deserialize(decoder: Decoder): FilterQuery = TODO("Not yet implemented")

    override fun serialize(encoder: Encoder, value: FilterQuery)
    {
        val surrogate = when (value) {
            is FilterQuery.Contains -> Surrogate(
                contains = value.contains,
            )
            is FilterQuery.DoesNotEqual -> Surrogate(
                does_not_equal = value.value,
            )
        }
        Surrogate.serializer().serialize(encoder, surrogate)
    }

    @Serializable
    private data class Surrogate(
        val contains: String? = null,
        val does_not_equal: String? = null,
    )
}
