package com.reneevandervelde.radio.units

@JvmInline
value class TransmitPower(
    val value: String,
) {
    companion object {
        val Off = TransmitPower("Off")
        val Low = TransmitPower("Low")
        val Medium = TransmitPower("Medium")
        val High = TransmitPower("High")
        val Max = TransmitPower("Max")
    }

    override fun toString(): String {
        return value
    }
}
