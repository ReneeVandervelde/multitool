package com.reneevandervelde.radio.units

@JvmInline
value class ColorCode(val key: String)
{
    val integer: Int get() = when (this) {
        CC0 -> 0
        CC1 -> 1
        CC2 -> 2
        CC3 -> 3
        CC4 -> 4
        CC5 -> 5
        CC6 -> 6
        CC7 -> 7
        CC8 -> 8
        CC9 -> 9
        CC10 -> 10
        CC11 -> 11
        CC12 -> 12
        CC13 -> 13
        CC14 -> 14
        CC15 -> 15
        else -> throw IllegalArgumentException("Unknown color code: $this")
    }
    companion object
    {
        val CC0 = ColorCode("CC0")
        val CC1 = ColorCode("CC1")
        val CC2 = ColorCode("CC2")
        val CC3 = ColorCode("CC3")
        val CC4 = ColorCode("CC4")
        val CC5 = ColorCode("CC5")
        val CC6 = ColorCode("CC6")
        val CC7 = ColorCode("CC7")
        val CC8 = ColorCode("CC8")
        val CC9 = ColorCode("CC9")
        val CC10 = ColorCode("CC10")
        val CC11 = ColorCode("CC11")
        val CC12 = ColorCode("CC12")
        val CC13 = ColorCode("CC13")
        val CC14 = ColorCode("CC14")
        val CC15 = ColorCode("CC15")
    }
}
