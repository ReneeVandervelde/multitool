package com.reneevandervelde.notion.page

import com.reneevandervelde.notion.property.PropertyName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = PageFilterSerializer::class)
sealed interface PageFilter
{
    data class Select(
        val property: PropertyName,
        val filter: ValueFilter,
    ): PageFilter

    data class Status(
        val property: PropertyName,
        val filter: ValueFilter,
    ): PageFilter

    data class MultiSelect(
        val property: PropertyName,
        val filter: ValueFilter,
    ): PageFilter

    data class Text(
        val property: PropertyName,
        val filter: TextFilter? = null,
    ): PageFilter

    data class Date(
        val property: PropertyName,
        val filter: ValueFilter,
    ): PageFilter

    data class Email(
        val property: PropertyName,
        val filter: ValueFilter,
    ): PageFilter

    data class PhoneNumber(
        val property: PropertyName,
        val filter: ValueFilter,
    ): PageFilter

    data class Checkbox(
        val property: PropertyName,
        val filter: CheckboxFilter,
    ): PageFilter

    data class CheckboxFormula(
        val property: PropertyName,
        val filter: CheckboxFilter,
    ): PageFilter

    data class Or(
        val filters: List<PageFilter>,
    ): PageFilter {
        constructor(vararg filters: PageFilter): this(filters.toList())
    }

    data class And(
        val filters: List<PageFilter>,
    ): PageFilter {
        constructor(vararg filters: PageFilter): this(filters.toList())
    }
}

internal class PageFilterSerializer: KSerializer<PageFilter>
{
    override val descriptor: SerialDescriptor = Surrogate.serializer().descriptor

    override fun deserialize(decoder: Decoder): PageFilter = TODO("Not yet implemented")

    override fun serialize(encoder: Encoder, value: PageFilter)
    {
        val surrogate = when (value) {
            is PageFilter.Select -> Surrogate(
                property = value.property,
                select = value.filter,
            )
            is PageFilter.MultiSelect -> Surrogate(
                property = value.property,
                multi_select = value.filter,
            )
            is PageFilter.Status -> Surrogate(
                property = value.property,
                status = value.filter,
            )
            is PageFilter.Text -> Surrogate(
                property = value.property,
                rich_text = value.filter,
            )
            is PageFilter.PhoneNumber -> Surrogate(
                property = value.property,
                phone_number = value.filter,
            )
            is PageFilter.Email -> Surrogate(
                property = value.property,
                email = value.filter,
            )
            is PageFilter.Date -> Surrogate(
                property = value.property,
                date = value.filter,
            )
            is PageFilter.Checkbox -> Surrogate(
                property = value.property,
                checkbox = value.filter,
            )
            is PageFilter.CheckboxFormula -> Surrogate(
                property = value.property,
                formula = Surrogate.Formula(
                    checkbox = value.filter,
                ),
            )
            is PageFilter.Or -> Surrogate(
                or = value.filters,
            )
            is PageFilter.And -> Surrogate(
                and = value.filters,
            )
        }
        Surrogate.serializer().serialize(encoder, surrogate)
    }

    @Serializable
    private data class Surrogate(
        val property: PropertyName? = null,
        val multi_select: ValueFilter? = null,
        val select: ValueFilter? = null,
        val status: ValueFilter? = null,
        val rich_text: TextFilter? = null,
        val phone_number: ValueFilter? = null,
        val email: ValueFilter? = null,
        val date: ValueFilter? = null,
        val checkbox: CheckboxFilter? = null,
        val formula: Formula? = null,
        val or: List<PageFilter>? = null,
        val and: List<PageFilter>? = null,
    ) {
        @Serializable
        data class Formula(
            val checkbox: CheckboxFilter? = null,
        )
    }
}
