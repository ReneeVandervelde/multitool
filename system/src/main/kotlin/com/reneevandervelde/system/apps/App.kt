package com.reneevandervelde.system.apps

import com.reneevandervelde.system.processes.Decision

interface App
{
    suspend fun enabled(): Decision
}
