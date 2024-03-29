package org.stypox.tridenta.repo.data

import org.stypox.tridenta.db.data.DbLine
import org.stypox.tridenta.db.data.DbStop
import org.stypox.tridenta.enums.Direction
import org.stypox.tridenta.enums.StopLineType
import org.stypox.tridenta.extractor.data.ExStopTime
import org.stypox.tridenta.extractor.data.ExTrip
import java.time.OffsetDateTime

/**
 * Same as [ExTrip] but with line and stop data loaded
 */
data class UiTrip(
    val delay: Int,
    val direction: Direction,
    val lastEventReceivedAt: OffsetDateTime?,
    val lineId: Int, // needed for when DbLine could not be loaded, and so it is null
    val line: DbLine?,
    val headSign: String,
    val tripId: String,
    val type: StopLineType,
    val completedStops: Int,
    val stopTimes: List<UiStopTime>,
    val busId: Int,
)

/**
 * Same as [ExStopTime] but with stop data loaded
 */
data class UiStopTime(
    val arrivalTime: OffsetDateTime?,
    val departureTime: OffsetDateTime?,
    val stop: DbStop?,
)
