package com.reneevandervelde.radio.units

import inkapplications.spondee.structure.Dimension
import kotlin.math.abs

fun <T> abs(dimension: Dimension<T>): T = dimension.withValue(abs(dimension.value.toDouble()))
