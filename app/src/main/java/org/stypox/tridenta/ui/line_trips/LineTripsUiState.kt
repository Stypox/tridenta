package org.stypox.tridenta.ui.line_trips

import org.stypox.tridenta.repo.data.UiLine
import org.stypox.tridenta.repo.data.UiTrip
import java.time.ZonedDateTime

data class LineTripsUiState(
    val line: UiLine?, // <- when null, nothing was loaded yet
    val tripsInDayCount: Int,
    val tripIndex: Int, // <- makes sense only if trip != null
    val trip: UiTrip?,
    val prevEnabled: Boolean,
    val nextEnabled: Boolean,
    val referenceDateTime: ZonedDateTime, // <- reuse only when trip != null
    val loading: Boolean
)