package org.stypox.tridenta.extractor.data

import org.stypox.tridenta.enums.Direction
import org.stypox.tridenta.enums.StopLineType
import java.time.OffsetDateTime

data class ExTrip(
    val delay: Int,
    val direction: Direction,
    val lastEventReceivedAt: OffsetDateTime?,
    val lineId: Int,
    val headSign: String,
    val tripId: String,
    val type: StopLineType,
    val completedStops: Int,
    val stopTimes: List<ExStopTime>,
) {
    fun getLastStopDateTime(): OffsetDateTime? {
        return stopTimes.asSequence().map { it.arrivalTime }.filter { it != null }.lastOrNull()
    }

    fun getServerSortDateTime(): OffsetDateTime? {
        return stopTimes.asSequence().map { it.arrivalTime }.filter { it != null }.firstOrNull()
    }

    companion object {
        const val DELAY_UNKNOWN = -1
    }
}

data class ExStopTime(
    val arrivalTime: OffsetDateTime?,
    val departureTime: OffsetDateTime?,
    val stopId: Int,
)