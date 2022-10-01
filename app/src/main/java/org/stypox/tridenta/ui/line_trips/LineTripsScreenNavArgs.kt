package org.stypox.tridenta.ui.line_trips

import org.stypox.tridenta.enums.StopLineType

data class LineTripsScreenNavArgs(
    val lineId: Int,
    val lineType: StopLineType
)