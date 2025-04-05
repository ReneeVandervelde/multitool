package com.reneevandervelde.system.apps.structures

import com.reneevandervelde.system.processes.Decision

interface App
{
    suspend fun enabled(): Decision
}
