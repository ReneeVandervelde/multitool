package com.reneevandervelde.notion.block

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = RichTextSerializer::class)
sealed interface RichText
{
    val plain_text: String?

    data class Text(
        override val plain_text: String?,
        val text: TextContent,
    ): RichText {
        @Serializable
        data class TextContent(
            val content: String,
            val link: String? = null,
        )
    }

    data class Unknown(
        override val plain_text: String?,
    ): RichText
}

internal class RichTextSerializer: KSerializer<RichText>
{
    override val descriptor: SerialDescriptor = Surrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: RichText) = TODO("Not implemented")

    override fun deserialize(decoder: Decoder): RichText
    {
        val surrogate = Surrogate.serializer().deserialize(decoder)
        return when (surrogate.type) {
            RichTextType.Text -> RichText.Text(
                plain_text = surrogate.plain_text,
                text = surrogate.text ?: error("Missing text content"),
            )
            else -> RichText.Unknown(
                plain_text = surrogate.plain_text,
            )
        }
    }

    @Serializable
    private data class Surrogate(
        val type: RichTextType,
        val plain_text: String? = null,
        val text: RichText.Text.TextContent? = null,
    )
}
