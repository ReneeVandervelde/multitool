package com.reneevandervelde.notion.property

import kotlinx.datetime.LocalDate
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

    data class Select(
        override val id: PropertyId,
        val select: SelectOption?
    ): Property

    data class Title(
        override val id: PropertyId,
        val title: List<RichTextBlock>,
    ): Property {
        fun toPlainText(): String = title.joinToString { it.plain_text.orEmpty() }
    }

    data class RichText(
        override val id: PropertyId,
        val rich_text: List<RichTextBlock>?,
    ): Property {
        fun toPlainText(): String? = rich_text?.joinToString { it.plain_text.orEmpty() }
    }

    data class Date(
        override val id: PropertyId,
        val start: String?,
    ): Property {
        fun toLocalDate() = start?.let(LocalDate::parse)
    }

    data class Number(
        override val id: PropertyId,
        val number: kotlin.Number?,
    ): Property

    data class PhoneNumber(
        override val id: PropertyId,
        val number: String?,
    ): Property

    data class Email(
        override val id: PropertyId,
        val email: String?,
    ): Property

    data class UnknownPropertyType(
        override val id: PropertyId,
        val type: PropertyType,
    ): Property

    data class UniqueId(
        override val id: PropertyId,
        val number: Int,
        val prefix: String? = null,
    ): Property
}

internal class PropertySerializer: KSerializer<Property>
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
            PropertyType.Number -> Property.Number(
                id = surrogate.id,
                number = surrogate.number
            )
            PropertyType.Select -> Property.Select(
                id = surrogate.id,
                select = surrogate.select
            )
            PropertyType.PhoneNumber -> Property.PhoneNumber(
                id = surrogate.id,
                number = surrogate.phone_number
            )
            PropertyType.Email -> Property.Email(
                id = surrogate.id,
                email = surrogate.email
            )
            PropertyType.Date -> Property.Date(
                id = surrogate.id,
                start = surrogate.date?.start
            )
            PropertyType.UniqueId -> Property.UniqueId(
                id = surrogate.id,
                number = surrogate.unique_id?.number ?: error("unique ID object must be present"),
                prefix = surrogate.unique_id.prefix
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
        val select: SelectOption? = null,
        val title: List<RichTextBlock>? = null,
        val rich_text: List<RichTextBlock>? = null,
        val number: Double? = null,
        val phone_number: String? = null,
        val email: String? = null,
        val date: Date? = null,
        val unique_id: UniqueId? = null,
    )
}
