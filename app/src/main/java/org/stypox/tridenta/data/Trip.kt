package org.stypox.tridenta.data

import java.time.OffsetDateTime

data class Trip(
    val delay: Int,
    val direction: Direction,
    val lastEventReceivedAt: OffsetDateTime?,
    val lineId: Int,
    val headSign: String,
    val tripId: String,
    val type: StopLineType,
    val completedStops: Int,
    val stopTimes: List<StopTime>,
) {
    companion object {
        const val DELAY_UNKNOWN = -1
    }
}

data class StopTime(
    val arrivalTime: OffsetDateTime,
    val departureTime: OffsetDateTime,
    val stopId: Int,
)