package com.reneevandervelde.system.processes

interface Operation
{
    val name: String
    suspend fun enabled(): Decision
    suspend fun runOperation()
}
