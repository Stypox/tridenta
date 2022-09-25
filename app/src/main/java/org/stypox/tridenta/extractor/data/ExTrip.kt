package org.stypox.tridenta.extractor.data

import org.stypox.tridenta.enums.Direction
import org.stypox.tridenta.enums.StopLineType
import java.time.Instant
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
    fun getHalfDateTime(): OffsetDateTime {
        return OffsetDateTime.ofInstant(
            Instant.ofEpochSecond(
                stopTimes.first().arrivalTime.toEpochSecond() + (
                        stopTimes.last().departureTime.toEpochSecond() -
                                stopTimes.first().arrivalTime.toEpochSecond()
                        ) / 2
            ),
            stopTimes.first().arrivalTime.offset
        )
    }

    fun getServerSortDateTime(): OffsetDateTime {
        return stopTimes.first().arrivalTime
    }

    companion object {
        const val DELAY_UNKNOWN = -1
    }
}

data class ExStopTime(
    val arrivalTime: OffsetDateTime,
    val departureTime: OffsetDateTime,
    val stopId: Int,
)