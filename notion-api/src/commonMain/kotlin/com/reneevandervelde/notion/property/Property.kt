package com.reneevandervelde.notion.property

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import com.reneevandervelde.notion.block.RichText as RichTextBlock

@Serializable(with = PropertySerializer::class)
sealed interface Property
{
    val id: PropertyId

    data class MultiSelect(
        override val id: PropertyId,
        val multi_select: List<MultiSelectOption>,
    ): Property

    data class Title(
        override val id: PropertyId,
        val title: List<RichTextBlock>,
    ): Property

    data class RichText(
        override val id: PropertyId,
        val rich_text: List<RichTextBlock>,
    ): Property

    data class UnknownPropertyType(
        override val id: PropertyId,
        val type: PropertyType,
    ): Property
}

private class PropertySerializer: KSerializer<Property>
{
    override val descriptor: SerialDescriptor = Surrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Property) = TODO("Not yet implemented")

    override fun deserialize(decoder: Decoder): Property
    {
        val surrogate = Surrogate.serializer().deserialize(decoder)
        return when (surrogate.type) {
            PropertyType.MultiSelect -> Property.MultiSelect(
                id = surrogate.id,
                multi_select = surrogate.multi_select ?: error("multi_select property must be present")
            )
            PropertyType.Title -> Property.Title(
                id = surrogate.id,
                title = surrogate.title ?: error("plain_text property must be present")
            )
            PropertyType.RichText -> Property.RichText(
                id = surrogate.id,
                rich_text = surrogate.rich_text ?: error("rich_text property must be present")
            )
            else -> Property.UnknownPropertyType(
                id = surrogate.id,
                type = surrogate.type,
            )
        }
    }

    @Serializable
    private data class Surrogate(
        val id: PropertyId,
        val type: PropertyType,
        val multi_select: List<MultiSelectOption>? = null,
        val title: List<RichTextBlock>? = null,
        val rich_text: List<RichTextBlock>? = null,
    )
}
