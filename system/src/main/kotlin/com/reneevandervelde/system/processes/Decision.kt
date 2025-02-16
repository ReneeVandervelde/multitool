package com.reneevandervelde.system.processes

sealed interface Decision
{
    val rationale: String
    data class Yes(override val rationale: String): Decision
    data class No(override val rationale: String): Decision
}
