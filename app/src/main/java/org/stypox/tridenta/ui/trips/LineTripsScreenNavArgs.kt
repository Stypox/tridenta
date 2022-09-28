package org.stypox.tridenta.ui.trips

import org.stypox.tridenta.enums.StopLineType

data class LineTripsScreenNavArgs(
    val lineId: Int,
    val lineType: StopLineType
)