package org.stypox.tridenta.ui.stop_trips

import org.stypox.tridenta.db.data.DbStop
import org.stypox.tridenta.repo.data.UiTrip
import java.time.ZonedDateTime

data class StopTripsUiState(
    val stop: DbStop?, // <- when null, nothing was loaded yet
    val tripIndex: Int, // <- makes sense only if trip != null
    val trip: UiTrip?,
    val prevEnabled: Boolean,
    val nextEnabled: Boolean,
    val referenceDateTime: ZonedDateTime, // <- reuse only when trip != null
    val loading: Boolean
)