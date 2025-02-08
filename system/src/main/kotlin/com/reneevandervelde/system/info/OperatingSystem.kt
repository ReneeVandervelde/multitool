package com.reneevandervelde.system.info

import kotlinx.datetime.LocalDate

sealed interface OperatingSystem
{
    sealed interface Linux: OperatingSystem
    {
        sealed interface Fedora: Linux
        {
            data class Silverblue(
                val versionId: Int,
                val supportEnd: LocalDate,
            ): Fedora
        }
    }
    data class MacOS(
        val version: String,
    ): OperatingSystem
}
