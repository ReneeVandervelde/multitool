package com.reneevandervelde.contacts.settings

import com.inkapplications.data.transformer.DataTransformer
import com.inkapplications.data.transformer.NoTransformation
import com.inkapplications.data.validator.DataValidator
import com.inkapplications.data.validator.PassingValidator
import com.inkapplications.data.validator.transformedWith
import regolith.data.settings.SettingCategory
import regolith.data.settings.SettingLevel
import regolith.data.settings.structure.DataSetting
import regolith.data.settings.structure.PrimitiveSetting
import regolith.data.settings.structure.SettingEntry
import regolith.data.settings.structure.StringSetting

data class SensitiveStringData<DATA>(
    override val key: String,
    override val name: String,
    override val defaultValue: DATA,
    override val dataTransformer: DataTransformer<String?, DATA>,
    override val category: SettingCategory? = null,
    override val description: String? = null,
    override val inputValidator: DataValidator<DATA> = PassingValidator,
    override val level: SettingLevel = SettingLevel.DEFAULT
): DataSetting<String?, DATA> {
    override fun toPrimitive(): PrimitiveSetting<String?>
    {
        return StringSetting(
            key = key,
            name = name,
            defaultValue = dataTransformer.reverseTransform(defaultValue),
            inputTransformer = NoTransformation(),
            inputValidator = inputValidator.transformedWith(dataTransformer),
            category = category,
            description = description,
            level = level,
        )
    }


    override fun toString(): String = "Setting($key)"
    override fun toEntry(value: DATA): SettingEntry<DATA, DataSetting<String?, DATA>>
    {
        return Entry(this, value)
    }

    data class Entry<DATA>(
        override val setting: SensitiveStringData<DATA>,
        override val value: DATA,
    ): SettingEntry<DATA, SensitiveStringData<DATA>>
}
