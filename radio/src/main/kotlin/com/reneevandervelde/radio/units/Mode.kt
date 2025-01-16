package com.reneevandervelde.radio.units

@JvmInline
value class Mode(
    val value: String
) {
    companion object {
        val AM = Mode("AM")
        val FM = Mode("FM")
        val NFM = Mode("NFM")
    }

    override fun toString(): String {
        return value
    }
}
