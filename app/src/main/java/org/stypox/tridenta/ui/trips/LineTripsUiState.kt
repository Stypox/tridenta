package org.stypox.tridenta.ui.trips

import org.stypox.tridenta.repo.data.UiTrip
import java.time.ZonedDateTime

data class LineTripsUiState(
    val tripsInDayCount: Int,
    val tripIndex: Int, // <- makes sense only if trip != null
    val trip: UiTrip?,
    val prevEnabled: Boolean,
    val nextEnabled: Boolean,
    val referenceDateTime: ZonedDateTime, // <- reuse only when trip != null
    val loading: Boolean
)