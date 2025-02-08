package com.reneevandervelde.system.processes

@JvmInline
value class ExitCode(val code: Int)
{
    override fun toString(): String
    {
        return code.toString()
    }

    companion object
    {
        val Success = ExitCode(0)
        val Unknown = ExitCode(1)
    }
}
