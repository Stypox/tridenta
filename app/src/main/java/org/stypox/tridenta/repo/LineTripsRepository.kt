package org.stypox.tridenta.repo

import org.stypox.tridenta.enums.StopLineType
import org.stypox.tridenta.extractor.data.ExTrip
import org.stypox.tridenta.extractor.Extractor
import org.stypox.tridenta.repo.data.UiStopTime
import org.stypox.tridenta.repo.data.UiTrip
import java.time.LocalDate
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class LineTripsRepository @Inject constructor(
    private val extractor: Extractor,
    private val linesRepository: LinesRepository,
    private val stopsRepository: StopsRepository
) {

    private val days = HashMap<Triple<Int, StopLineType, LocalDate>, TripsInDayMap>()

    /**
     * @return the triple (number of trips in the day, index of the trip, the trip)
     */
    fun getUiTrip(
        lineId: Int,
        lineType: StopLineType,
        referenceDateTime: ZonedDateTime
    ): Triple<Int, Int, UiTrip> {
        val key = Triple(lineId, lineType, referenceDateTime.toLocalDate())
        if (days[key]?.isClosestOnServerLoaded(referenceDateTime) != true) {
            handleNewTrips(
                key = key,
                trips = extractor.getTripsByLine(
                    lineId = lineId,
                    lineType = lineType,
                    referenceDateTime = referenceDateTime,
                    limit = TRIPS_BATCH_SIZE
                )
            )
        }

        val day = days[key]!!
        return day.getClosestAtHalf(referenceDateTime).let {
            Triple(day.tripsInDayCount, it.first, loadUiTripFromExTrip(it.second))
        }
    }

    fun getUiTrip(
        lineId: Int,
        lineType: StopLineType,
        referenceDateTime: ZonedDateTime,
        index: Int
    ): UiTrip {
        val key = Triple(lineId, lineType, referenceDateTime.toLocalDate())
        val tripsInDay = days[key]
        if (tripsInDay?.containsKey(index) != true) {
            handleNewTrips(
                key = key,
                trips = extractor.getTripsByLine(
                    lineId = lineId,
                    lineType = lineType,
                    referenceDateTime = referenceDateTime,
                    indexFromInclusive = maxOf(
                        0,
                        index - TRIPS_BATCH_SIZE / 2,
                        tripsInDay?.let {
                            it.keys.asSequence().filter { i -> i < index }.maxOrNull()?.plus(1)
                        } ?: 0
                    ),
                    indexToInclusive = minOf(
                        tripsInDay?.tripsInDayCount ?: Int.MAX_VALUE,
                        index + TRIPS_BATCH_SIZE / 2,
                        tripsInDay?.let {
                            it.keys.asSequence().filter { i -> i > index }.minOrNull()?.minus(1)
                        } ?: Int.MAX_VALUE
                    ),
                )
            )
        }

        return loadUiTripFromExTrip(days[key]!![index]!!)
    }


    private fun handleNewTrips(
        key: Triple<Int, StopLineType, LocalDate>,
        trips: Pair<Int, List<Pair<Int, ExTrip>>>
    ) {
        days.getOrPut(key) { TripsInDayMap(trips.first) }
            .let { tripsInDay ->
                trips.second.forEach { indexTripPair ->
                    tripsInDay[indexTripPair.first] = indexTripPair.second
                }
            }
    }

    private fun loadUiTripFromExTrip(exTrip: ExTrip): UiTrip {
        return UiTrip(
            delay = exTrip.delay,
            direction = exTrip.direction,
            lastEventReceivedAt = exTrip.lastEventReceivedAt,
            line = linesRepository.getDbLine(exTrip.lineId, exTrip.type),
            headSign = exTrip.headSign,
            tripId = exTrip.tripId,
            type = exTrip.type,
            completedStops = exTrip.completedStops,
            stopTimes = exTrip.stopTimes.map { exStopTime ->
                UiStopTime(
                    arrivalTime = exStopTime.arrivalTime,
                    departureTime = exStopTime.departureTime,
                    stop = stopsRepository.getDbStop(exStopTime.stopId, exTrip.type)
                )
            }
        )
    }


    private class TripsInDayMap(var tripsInDayCount: Int) : HashMap<Int, ExTrip>() {

        /**
         * @return whether the trips closest to the provided date time have already been loaded; the
         * comparison is done with the date time used by the server to sort trips.
         */
        fun isClosestOnServerLoaded(referenceDateTime: ZonedDateTime): Boolean {
            val closestTwo = this.asSequence()
                .sortedWith(
                    Comparator.comparing<Map.Entry<Int, ExTrip>?, Long?> {
                        abs((it.value.getServerSortDateTime()?.toEpochSecond() ?: 0) -
                                referenceDateTime.toEpochSecond())
                    }.then(Comparator.comparing { it.key })
                )
                .take(2)
                .sortedBy { it.key }
                .toList()

            if (closestTwo.size < 2) {
                // not enough data to make sure the closest trip is loaded
                return false
            }

            // if this is true, then the closest trip is ready, as the date is in between two trips
            // with subsequent indexes, so there are surely no not-loaded trips in the middle
            return (closestTwo[0].value.getServerSortDateTime()?.toEpochSecond() ?: 0) <=
                    referenceDateTime.toEpochSecond() &&
                    (closestTwo[1].value.getServerSortDateTime()?.toEpochSecond() ?: 0) >=
                    referenceDateTime.toEpochSecond() &&
                    closestTwo[0].key + 1 == closestTwo[1].key
        }

        /**
         * @return the trip whose half date time is closest to the provided date time
         */
        fun getClosestAtHalf(referenceDateTime: ZonedDateTime): Pair<Int, ExTrip> {
            return this.asSequence()
                .sortedWith(
                    Comparator.comparing<Map.Entry<Int, ExTrip>?, Long?> {
                        abs((it.value.getHalfDateTime()?.toEpochSecond() ?: 0) -
                                referenceDateTime.toEpochSecond())
                    }.then(Comparator.comparing { it.key })
                )
                .first()
                .toPair()
        }
    }

    companion object {
        // how many trips to fetch at the same time
        const val TRIPS_BATCH_SIZE = 8
    }
}
