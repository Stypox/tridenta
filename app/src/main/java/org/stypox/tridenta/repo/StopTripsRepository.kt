package org.stypox.tridenta.repo

import org.stypox.tridenta.enums.StopLineType
import org.stypox.tridenta.extractor.Extractor
import org.stypox.tridenta.extractor.data.ExTrip
import org.stypox.tridenta.repo.data.UiTrip
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StopTripsRepository @Inject constructor(
    private val extractor: Extractor,
    private val linesRepository: LinesRepository,
    private val stopsRepository: StopsRepository
) {

    fun getTrips(
        stopId: Int,
        stopType: StopLineType,
        referenceDateTime: ZonedDateTime
    ): TripsAtDateTimeList {
        return TripsAtDateTimeList(
            exTrips = extractor.getTripsByStop(
                stopId = stopId,
                stopType = stopType,
                referenceDateTime = referenceDateTime,
                limit = STOP_TRIPS_BATCH_SIZE
            ).toMutableList(),
            extractor = extractor,
            linesRepository = linesRepository,
            stopsRepository = stopsRepository
        )
    }


    class TripsAtDateTimeList(
        private val exTrips: MutableList<ExTrip>,
        private val extractor: Extractor,
        linesRepository: LinesRepository,
        stopsRepository: StopsRepository
    ) : TripsRepository(linesRepository, stopsRepository) {
        val tripCount: Int
            get() = exTrips.size
        
        fun getUiTripAtIndex(index: Int): UiTrip {
            return loadUiTripFromExTrip(exTrips[index])
        }

        fun reloadUiTrip(uiTrip: UiTrip, index: Int, referenceDateTime: ZonedDateTime): UiTrip {
            val exTrip = extractor.getTripById(uiTrip.tripId, referenceDateTime)
            exTrips[index] = exTrip

            // replace only info that might have changed and avoid loading line and stops data again
            return uiTrip.copy(
                delay = exTrip.delay,
                direction = exTrip.direction,
                lastEventReceivedAt = exTrip.lastEventReceivedAt,
                headSign = exTrip.headSign,
                completedStops = exTrip.completedStops
            )
        }
    }

    companion object {
        // how many trips to fetch at the same time
        const val STOP_TRIPS_BATCH_SIZE = 12
    }
}
