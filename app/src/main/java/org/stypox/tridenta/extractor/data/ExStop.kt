package org.stypox.tridenta.extractor.data

import org.stypox.tridenta.enums.StopLineType

data class ExStop(
    val stopId: Int,
    val type: StopLineType,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val street: String,
    val town: String,
    val wheelchairAccessible: Boolean,
    val lines: List<Pair<Int, StopLineType>>,
)