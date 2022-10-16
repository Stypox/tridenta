package org.stypox.tridenta.repo

import org.stypox.tridenta.enums.StopLineType
import org.stypox.tridenta.extractor.Extractor
import org.stypox.tridenta.extractor.data.ExTrip
import org.stypox.tridenta.repo.data.UiTrip
import java.time.LocalDate
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class LineTripsRepository @Inject constructor(
    private val extractor: Extractor,
    linesRepository: LinesRepository,
    stopsRepository: StopsRepository
) : TripsRepository(linesRepository, stopsRepository) {

    private val days = HashMap<Triple<Int, StopLineType, LocalDate>, TripsInDayMap>()

    /**
     * @return the triple (number of trips in the day, index of the trip, the trip)
     */
    fun getUiTrip(
        lineId: Int,
        lineType: StopLineType,
        referenceDateTime: ZonedDateTime
    ): Triple<Int, Int, UiTrip?> {
        val key = Triple(lineId, lineType, referenceDateTime.toLocalDate())
        if (days[key]?.isClosestOnServerLoaded(referenceDateTime) != true) {
            handleNewTrips(
                key = key,
                trips = extractor.getTripsByLine(
                    lineId = lineId,
                    lineType = lineType,
                    referenceDateTime = referenceDateTime,
                    limit = LINE_TRIPS_BATCH_SIZE
                )
            )
        }

        val day = days[key]!!
        return day.getTripToShowInitially(referenceDateTime)
            ?.let { Triple(day.tripsInDayCount, it.first, loadUiTripFromExTrip(it.second)) }
            ?: Triple(day.tripsInDayCount, 0, null)
    }

    /**
     * Returns a pair with the ui trip and whether the trip was loaded from network
     */
    fun getUiTrip(
        lineId: Int,
        lineType: StopLineType,
        referenceDateTime: ZonedDateTime,
        index: Int
    ): Pair<UiTrip, Boolean> {
        val key = Triple(lineId, lineType, referenceDateTime.toLocalDate())
        val tripsInDay = days[key]
        val loadFromNetwork = tripsInDay?.containsKey(index) != true

        if (loadFromNetwork) {
            handleNewTrips(
                key = key,
                trips = extractor.getTripsByLine(
                    lineId = lineId,
                    lineType = lineType,
                    referenceDateTime = referenceDateTime,
                    indexFromInclusive = maxOf(
                        0,
                        index - LINE_TRIPS_BATCH_SIZE / 2,
                        tripsInDay?.let {
                            it.keys.asSequence().filter { i -> i < index }.maxOrNull()?.plus(1)
                        } ?: 0
                    ),
                    indexToInclusive = minOf(
                        tripsInDay?.tripsInDayCount ?: Int.MAX_VALUE,
                        index + LINE_TRIPS_BATCH_SIZE / 2,
                        tripsInDay?.let {
                            it.keys.asSequence().filter { i -> i > index }.minOrNull()?.minus(1)
                        } ?: Int.MAX_VALUE
                    ),
                )
            )
        }

        return Pair(loadUiTripFromExTrip(days[key]!![index]!!), loadFromNetwork)
    }

    fun reloadUiTrip(uiTrip: UiTrip, index: Int, referenceDateTime: ZonedDateTime): UiTrip {
        val exTrip = extractor.getTripById(uiTrip.tripId, referenceDateTime)
        val key = Triple(uiTrip.line.lineId, uiTrip.line.type, referenceDateTime.toLocalDate())
        days[key]?.put(index, exTrip)

        // replace only info that might have changed and avoid loading line and stops data again
        return uiTrip.copy(
            delay = exTrip.delay,
            direction = exTrip.direction,
            lastEventReceivedAt = exTrip.lastEventReceivedAt,
            headSign = exTrip.headSign,
            completedStops = exTrip.completedStops
        )
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
         * @return the most suitable trip to show initially based on the provided date time
         */
        fun getTripToShowInitially(referenceDateTime: ZonedDateTime): Pair<Int, ExTrip>? {
            return this.asSequence()
                .sortedWith(
                    Comparator.comparing<Map.Entry<Int, ExTrip>?, Long?> {
                        ((it.value.getLastStopDateTime()?.toEpochSecond() ?: 0) -
                                referenceDateTime.toEpochSecond()).let { secondsInThePast ->
                            if (secondsInThePast < -120 /* two minutes in the past */) {
                                // make sure trips completed some time ago are not shown initially
                                1000000000 - secondsInThePast
                            } else {
                                secondsInThePast
                            }
                        }
                    }.then(Comparator.comparing { it.key })
                )
                .firstOrNull()
                ?.toPair()
        }
    }

    companion object {
        // how many trips to fetch at the same time
        const val LINE_TRIPS_BATCH_SIZE = 8
    }
}
