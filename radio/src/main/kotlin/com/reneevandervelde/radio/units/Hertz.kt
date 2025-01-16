package com.reneevandervelde.radio.units

import inkapplications.spondee.structure.*

@JvmInline
value class Hertz(
    override val value: Number
): Frequency, Dimension<Hertz> {
    override fun toHertz(): Hertz = this
    override val symbol: String get() = "Hz"
    override fun withValue(value: Number): Hertz = Hertz(value)
    override fun toString(): String = format()
}

val Number.hertz get() = Hertz(this)
val Number.kilohertz get() = scale(Kilo).hertz
val Number.megahertz get() = scale(Mega).hertz
