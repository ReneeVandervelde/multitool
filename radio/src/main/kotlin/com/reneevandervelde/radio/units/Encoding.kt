package com.reneevandervelde.radio.units

@JvmInline
value class Encoding(val key: String)
{
    companion object
    {
        val Analog = Encoding("Analog")
        val DmrRepeater = Encoding("DMR Repeater")
        val DmrSimplex = Encoding("DMR Simplex")
    }
}
