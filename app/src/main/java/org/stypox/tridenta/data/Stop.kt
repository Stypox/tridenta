package org.stypox.tridenta.data

data class Stop(
    val stopId: Int,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val address: String,
    val type: StopLineType,
    val lines: List<Line>,
)