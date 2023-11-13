package org.stypox.tridenta.repo

import org.stypox.tridenta.enums.Direction
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
     * @param directionFilter can be [Direction.Forward], [Direction.Backward] or
     * [Direction.ForwardAndBackward]
     * @return the triple (number of trips in the day, index of the trip, the trip)
     */
    fun getUiTrip(
        lineId: Int,
        lineType: StopLineType,
        referenceDateTime: ZonedDateTime,
        directionFilter: Direction,
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
        return day.getTripToShowInitially(referenceDateTime, directionFilter)
            ?.let { Triple(day.tripsInDayCount, it.first, loadUiTripFromExTrip(it.second)) }
            ?: Triple(day.tripsInDayCount, 0, null)
    }

    private fun loadMoreTripsAtIndex(
        key: Triple<Int, StopLineType, LocalDate>,
        referenceDateTime: ZonedDateTime,
        index: Int,
    ): TripsInDayMap {
        val tripsInDay = days[key]
        return handleNewTrips(
            key = key,
            trips = extractor.getTripsByLine(
                lineId = key.first,
                lineType = key.second,
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

    /**
     * Get or load a trip at the provided index, without filtering by direction (simple case)
     * @return a pair (ui trip, whether the trip was loaded from network)
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
            loadMoreTripsAtIndex(key, referenceDateTime, index)
        }

        return Pair(loadUiTripFromExTrip(days[key]!![index]!!), loadFromNetwork)
    }

    private fun generateNearbyIndices(
        index: Int,
        prevIndex: Int,
        tripsInDayCount: Int,
        direction: Direction
    ) = iterator {
        if (index < prevIndex) {
            // the last index change was decreasing, so keep decreasing => 5,4,3,...
            yieldAll(index.downTo(0))

        } else if (index > prevIndex) {
            // the last index change was increasing, so keep increasing => 5,6,7,...
            yieldAll(index..tripsInDayCount)


        } else /* prevIndex == index*/ {
            // Find the closest trip starting from the current index in both directions.
            // Two slightly different sequences are generated based on the direction, to make it
            // so that switching direction multiple times cycles between the same two trips:
            // - Forward => 5,6,4,7,3...
            // - Backward => 5,4,6,3,7,...

            if (direction == Direction.Forward) {
                var newIndexDown = index
                var newIndexUp = index + 1
                while (newIndexDown >= 0 || newIndexUp < tripsInDayCount) {
                    if (newIndexDown >= 0) {
                        yield(newIndexDown)
                        --newIndexDown
                    }
                    if (newIndexUp < tripsInDayCount) {
                        yield(newIndexUp)
                        ++newIndexUp
                    }
                }

            } else /* direction == Direction.Backward */ {
                var newIndexDown = index - 1
                var newIndexUp = index
                while (newIndexDown >= 0 || newIndexUp < tripsInDayCount) {
                    if (newIndexUp < tripsInDayCount) {
                        yield(newIndexUp)
                        ++newIndexUp
                    }
                    if (newIndexDown >= 0) {
                        yield(newIndexDown)
                        --newIndexDown
                    }
                }
            }
        }
    }

    /**
     * Get or load a trip at the provided index in the specific direction. If the trip at the
     * provided index is not in the correct direction, nearby indices will be taken into
     * consideration, based on what the last index change was:
     * - `index < prevIndex` => only try lower indices (e.g. 5,4,3,...)
     * - `index = prevIndex` => try nearby indices in a spiral (e.g. 5,4,6,3,7,...)
     * - `index > prevIndex` => only try higher indices (e.g. 5,6,7,...)
     *
     * While searching for nearby trips, new trips might be loaded from network, but this will
     * happen at most once, as it's highly unlikely that after loading more items from network and
     * finding none of the correct type, there will be some even further.
     *
     * @param direction must be one of [Direction.Forward] or [Direction.Backward]
     * @return a triple (ui trip, actual trip index, whether the trip was loaded from network)
     */
    fun getUiTripWithDirection(
        lineId: Int,
        lineType: StopLineType,
        referenceDateTime: ZonedDateTime,
        direction: Direction,
        index: Int,
        prevIndex: Int,
    ): Triple<UiTrip, Int, Boolean>? {
        val key = Triple(lineId, lineType, referenceDateTime.toLocalDate())
        var loadedFromNetworkOnce = false

        val initialTripsInDay = days[key];
        val tripsInDay = if (initialTripsInDay == null) {
            // no trips for this day have been loaded at all so far, so load for the first time
            loadedFromNetworkOnce = true
            loadMoreTripsAtIndex(key, referenceDateTime, index)
        } else {
            initialTripsInDay
        }

        val nearbyIndices =
            generateNearbyIndices(index, prevIndex, tripsInDay.tripsInDayCount, direction)

        nearbyIndices.forEach { newIndex ->
            // load from network at most once
            if (!loadedFromNetworkOnce && tripsInDay[newIndex] == null) {
                loadedFromNetworkOnce = true
                loadMoreTripsAtIndex(key, referenceDateTime, newIndex)
            }

            // if the trip is null it means we would need to load from network a second time
            val trip = tripsInDay[newIndex] ?: return null
            if (trip.direction == direction) {
                return Triple(loadUiTripFromExTrip(trip), newIndex, loadedFromNetworkOnce)
            }
        }

        // we went out of bounds, keep the original index
        return null
    }

    fun reloadUiTrip(uiTrip: UiTrip, index: Int, referenceDateTime: ZonedDateTime): UiTrip {
        val exTrip = extractor.getTripById(uiTrip.tripId, referenceDateTime)
        if (uiTrip.lastEventReceivedAt != null && exTrip.lastEventReceivedAt == null) {
            // keep previous trip intact, since the just loaded trip has no live information
            return uiTrip
        }

        val key = Triple(uiTrip.lineId, uiTrip.type, referenceDateTime.toLocalDate())
        days[key]?.put(index, exTrip)

        // replace only info that might have changed and avoid loading line and stops data again
        return uiTrip.copy(
            delay = exTrip.delay,
            direction = exTrip.direction,
            lastEventReceivedAt = exTrip.lastEventReceivedAt,
            headSign = exTrip.headSign,
            completedStops = exTrip.completedStops,
            busId = exTrip.busId,
        )
    }


    private fun handleNewTrips(
        key: Triple<Int, StopLineType, LocalDate>,
        trips: Pair<Int, List<Pair<Int, ExTrip>>>
    ): TripsInDayMap {
        return days.getOrPut(key) { TripsInDayMap(trips.first) }
            .also { tripsInDay ->
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
                        abs(
                            (it.value.getServerSortDateTime()?.toEpochSecond() ?: 0) -
                                    referenceDateTime.toEpochSecond()
                        )
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
         * @param directionFilter only consider trips in this direction (if different from
         * [Direction.ForwardAndBackward])
         * @return the most suitable trip to show initially based on the provided date time
         */
        fun getTripToShowInitially(
            referenceDateTime: ZonedDateTime,
            directionFilter: Direction,
        ): Pair<Int, ExTrip>? {
            return if (directionFilter == Direction.ForwardAndBackward) {
                this.asSequence()
            } else {
                this.asSequence()
                    // exclude trips in the wrong direction
                    .filter { directionFilter == it.value.direction }
            }
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
