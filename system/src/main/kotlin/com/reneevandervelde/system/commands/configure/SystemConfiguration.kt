package com.reneevandervelde.system.commands.configure

interface SystemConfiguration
{
    val id: String
    suspend fun getStatus(): ConfigurationStatus
}
