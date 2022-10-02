package org.stypox.tridenta.extractor

import org.json.JSONArray
import org.json.JSONObject
import org.stypox.tridenta.enums.*
import org.stypox.tridenta.extractor.data.ExLine
import org.stypox.tridenta.extractor.data.ExStop
import org.stypox.tridenta.extractor.data.ExTrip
import org.stypox.tridenta.extractor.data.shortNameComparator
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Extractor @Inject constructor(private val httpClient: HttpClient) {

    /**
     * Get all existing stops. The response's size will be ~2MB, so persist the returned data! The
     * stops' lines will be sorted by short name using [shortNameComparator].
     *
     * @param limit return only this number of results. A negative value means "no limitation".
     * Providing this parameter reduces the response size, though this is useful only for testing
     * purposes, as otherwise it does not make sense to fetch only some of the stops at random.
     * @return a list of stops, with their [ExStop.stopId] and [ExStop.type] filled in and usable in
     * queries to other APIs involving stops
     */
    fun getStops(limit: Int = -1): List<ExStop> {
        val params = if (limit < 0) "" else "?size=$limit"
        return JSONArray(httpClient.fetchJson(BASE_URL + STOPS_PATH + params))
            .map(::stopFromJSONObject)
    }

    /**
     * Get all lines belonging to the provided [areas], sorted by short name using
     * [shortNameComparator].
     *
     * @param areas the areas for which to fetch lines
     * @return a list of lines, with their [ExLine.lineId] and [ExLine.type] filled in and usable in
     * queries to other APIs involving lines
     */
    fun getLines(areas: Array<Area> = Area.values()): List<ExLine> {
        val params = "?areas=" + areas.map { it.value }.joinToString(",")
        return JSONArray(httpClient.fetchJson(BASE_URL + LINES_PATH + params))
            .map(::lineFromJSONObject)
            .sortedWith(::shortNameComparator)
    }

    /**
     * Get some trips that pass through the provided stop.
     *
     * @param stopId the id of the stop as returned in [getStops]
     * @param stopType the type of the stop as returned in [getStops]
     * @param referenceDateTime returned trips will take place near this time; also used to produce
     * the dates in the trips' [org.stypox.tridenta.extractor.data.ExStopTime]s
     * @param limit return only this number of trips
     * @return a list of trips with [ExTrip.tripId] filled in and usable to get updates via
     * [getTripById]
     */
    fun getTripsByStop(
        stopId: Int,
        stopType: StopLineType,
        referenceDateTime: ZonedDateTime,
        limit: Int
    ): List<ExTrip> {
        return getTrips(
            stopType,
            referenceDateTime,
            "&stopId=$stopId&limit=$limit",
            ::tripFromJSONObject
        ).second
    }

    /**
     * Get some trips performed by the provided line. Useful for time-based queries.
     *
     * @param lineId the id of the line as returned in [getLines]
     * @param lineType the type of the line as returned in [getLines]
     * @param referenceDateTime returned trips will take place near this time; also used to produce
     * the dates in the trips' [org.stypox.tridenta.extractor.data.ExStopTime]s
     * @param limit return only this number of trips
     * @return a pair with the total number of trips in this specific day as first item, and as
     * second item a list of trips (with [ExTrip.tripId] filled in and usable to get updates via
     * [getTripById]) paired with integers (representing the trip index and usable in the other
     * [getTripsByLine] to scroll through the list of trips of this specific day)
     */
    fun getTripsByLine(
        lineId: Int,
        lineType: StopLineType,
        referenceDateTime: ZonedDateTime,
        limit: Int,
    ): Pair<Int, List<Pair<Int, ExTrip>>> {
        return getTrips(
            lineType,
            referenceDateTime,
            "&routeId=$lineId&limit=$limit",
            ::tripWithIndexFromJSONObject
        )
    }

    /**
     * Get some trips performed by the provided line. Once a time-based query has been performed
     * using the other [getTripsByLine], this method can be used to scroll through the trips list of
     * that day (use the same [referenceDateTime]).
     *
     * @param lineId the id of the line as returned in [getLines]
     * @param lineType the type of the line as returned in [getLines]
     * @param referenceDateTime returned trips will take place near this time; also used to produce
     * the dates in the trips' [org.stypox.tridenta.extractor.data.ExStopTime]s
     * @param indexFromInclusive the index (in the day identified by [referenceDateTime]) of the
     * first trip to return, inclusive
     * @param indexToInclusive the index (in the day identified by [referenceDateTime]) of the last
     * trip to return, inclusive
     * @return a pair with the total number of trips in this specific day as first item, and as
     * second item a list of trips (with [ExTrip.tripId] filled in and usable to get updates via
     * [getTripById]) paired with integers (ranging from [indexFromInclusive] to [indexToInclusive],
     * both extrema included, and representing the trip index)
     */
    fun getTripsByLine(
        lineId: Int,
        lineType: StopLineType,
        referenceDateTime: ZonedDateTime,
        indexFromInclusive: Int,
        indexToInclusive: Int
    ): Pair<Int, List<Pair<Int, ExTrip>>> {
        return getTrips(
            lineType,
            referenceDateTime,
            "&routeId=$lineId&indiceDa=$indexFromInclusive&indiceA=$indexToInclusive",
            ::tripWithIndexFromJSONObject
        )
    }

    private fun <R> getTrips(
        stopLineType: StopLineType,
        referenceDateTime: ZonedDateTime,
        otherParams: String,
        transform: (JSONObject, ZonedTimeHelper) -> R
    ): Pair<Int, List<R>> {
        val zonedTimeHelper = ZonedTimeHelper(referenceDateTime)
        val params = "?type=${stopLineType.value}&refDateTime=${referenceDateTime.toInstant()}"
        val trips = JSONArray(httpClient.fetchJson(BASE_URL + TRIPS_PATH + params + otherParams))
        return Pair(
            trips.optJSONObject(0)?.optInt("totaleCorseInLista", 0) ?: 0,
            trips.map { it: JSONObject -> transform(it, zonedTimeHelper) }
        )
    }

    /**
     * Get updates about the provided trip.
     *
     * @param tripId the id of the trip as returned in [getTripsByStop] or [getTripsByLine]
     * @param referenceDateTime used to produce the dates in the trips'
     * [org.stypox.tridenta.extractor.data.ExStopTime]s
     * @return the requested trip (obviously with the latest information)
     */
    fun getTripById(tripId: String, referenceDateTime: ZonedDateTime): ExTrip {
        return tripFromJSONObject(
            JSONObject(httpClient.fetchJson(BASE_URL + TRIP_PATH + tripId)),
            ZonedTimeHelper(referenceDateTime)
        )
    }


    companion object {
        private const val BASE_URL = "https://app-tpl.tndigit.it/gtlservice/"
        private const val STOPS_PATH = "stops"
        private const val LINES_PATH = "routes"
        private const val BIKE_SHARING_PATH = "poi/bikesharing"
        private const val DIRECTION_PATH = "direction"
        private const val RAILWAY_STATIONS_PATH = "poi/contextaware/stazioni"
        private const val TRIPS_PATH = "trips_new"
        private const val TRIP_PATH = "trips/"
    }
}