package com.reneevandervelde.system.commands.configure

sealed interface ConfigurationStatus
{
    data object NotConfigured: ConfigurationStatus
    data object Configured: ConfigurationStatus
    data class Error(
        val message: String,
        val cause: Throwable? = null,
    ): ConfigurationStatus
}
