package com.reneevandervelde.notion.page

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = CheckboxFilterSerializer::class)
sealed interface CheckboxFilter
{
    data class Equals(
        val value: Boolean,
    ): CheckboxFilter

    data class DoesNotEqual(
        val value: Boolean,
    ): CheckboxFilter
}

internal class CheckboxFilterSerializer: KSerializer<CheckboxFilter>
{
    override val descriptor: SerialDescriptor = Surrogate.serializer().descriptor

    override fun deserialize(decoder: Decoder): CheckboxFilter = TODO()

    override fun serialize(encoder: Encoder, value: CheckboxFilter)
    {
        val surrogate = when (value) {
            is CheckboxFilter.Equals -> Surrogate(
                equals = value.value,
            )
            is CheckboxFilter.DoesNotEqual -> Surrogate(
                does_not_equal = value.value,
            )
        }
        Surrogate.serializer().serialize(encoder, surrogate)
    }

    @Serializable
    private data class Surrogate(
        val does_not_equal: Boolean? = null,
        val equals: Boolean? = null,
    )
}
