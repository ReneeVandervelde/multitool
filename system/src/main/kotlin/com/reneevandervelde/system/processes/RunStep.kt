package com.reneevandervelde.system.processes

interface RunStep {
    suspend fun enabled(): Boolean
    suspend fun runStep()
}
