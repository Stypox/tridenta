package org.stypox.tridenta.ui.stop_trips

import org.stypox.tridenta.enums.StopLineType

data class StopTripsScreenNavArgs(
    val stopId: Int,
    val stopType: StopLineType
)