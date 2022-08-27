package org.stypox.tridenta.extractor

import org.json.JSONArray
import org.json.JSONObject
import org.stypox.tridenta.data.*
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Extractor @Inject constructor(private val httpClient: HttpClient) {

    /**
     * Get all existing stops. The response's size will be ~2MB, so persist the returned data!
     *
     * @param limit return only this number of results. A negative value means "no limitation".
     * Providing this parameter reduces the response size, though this is useful only for testing
     * purposes, as otherwise it does not make sense to fetch only some of the stops at random.
     * @return a list of stops, with their [Stop.stopId] and [Stop.type] filled in and usable in
     * queries to other APIs involving stops
     */
    fun getStops(limit: Int = -1): List<Stop> {
        val params = if (limit < 0) "" else "?size=$limit"
        return JSONArray(httpClient.fetchJson(BASE_URL + STOPS_PATH + params))
            .map(::stopFromJSONObject)
    }

    /**
     * Get all lines belonging to the provided [areas].
     *
     * @param areas the areas for which to fetch lines
     * @return a list of lines, with their [Line.lineId] and [Line.type] filled in and usable in
     * queries to other APIs involving lines
     */
    fun getLines(areas: Array<Area> = Area.values()): List<Line> {
        val params = "?areas=" + areas.map { it.value }.joinToString(",")
        return JSONArray(httpClient.fetchJson(BASE_URL + LINES_PATH + params))
            .map(::lineFromJSONObject)
    }

    /**
     * Get some trips that pass through the provided stop.
     *
     * @param stopId the id of the stop as returned in [getStops]
     * @param stopType the type of the stop as returned in [getStops]
     * @param referenceDateTime returned trips will take place near this time; also used to produce
     * the dates in the trips' [StopTime]s
     * @param limit return only this number of trips
     * @return a list of trips with [Trip.tripId] filled in and usable to get updates via
     * [getTripById]
     */
    fun getTripsByStop(
        stopId: Int,
        stopType: StopLineType,
        referenceDateTime: LocalDateTime,
        limit: Int
    ): List<Trip> {
        return getTrips(
            stopType,
            referenceDateTime,
            "&stopId=$stopId&limit=$limit",
            ::tripFromJSONObject
        )
    }

    /**
     * Get some trips performed by the provided line. Useful for time-based queries.
     *
     * @param lineId the id of the line as returned in [getLines]
     * @param lineType the type of the line as returned in [getLines]
     * @param referenceDateTime returned trips will take place near this time; also used to produce
     * the dates in the trips' [StopTime]s
     * @param limit return only this number of trips
     * @return a list of trips (with [Trip.tripId] filled in and usable to get updates via
     * [getTripById]) paired with integers (representing the trip index and usable in the other
     * [getTripsByLine] to scroll through a list of trips in a specific day)
     */
    fun getTripsByLine(
        lineId: Int,
        lineType: StopLineType,
        referenceDateTime: LocalDateTime,
        limit: Int,
    ): List<Pair<Int, Trip>> {
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
     * the dates in the trips' [StopTime]s
     * @param indexFromInclusive the index (in the day identified by [referenceDateTime]) of the
     * first trip to return, inclusive
     * @param indexToInclusive the index (in the day identified by [referenceDateTime]) of the last
     * trip to return, inclusive
     * @return a list of trips (with [Trip.tripId] filled in and usable to get updates via
     * [getTripById]) paired with integers (ranging from [indexFromInclusive] to [indexToInclusive],
     * both extrema included, and representing the trip index)
     */
    fun getTripsByLine(
        lineId: Int,
        lineType: StopLineType,
        referenceDateTime: LocalDateTime,
        indexFromInclusive: Int,
        indexToInclusive: Int
    ): List<Pair<Int, Trip>> {
        return getTrips(
            lineType,
            referenceDateTime,
            "&routeId=$lineId&indiceDa=$indexFromInclusive&indiceA=$indexToInclusive",
            ::tripWithIndexFromJSONObject
        )
    }

    private fun <R> getTrips(
        stopLineType: StopLineType,
        referenceDateTime: LocalDateTime,
        otherParams: String,
        transform: (JSONObject, ZonedTimeHelper) -> R
    ): List<R> {
        val romeReferenceDateTime = localToRomeDateTime(referenceDateTime)
        val zonedTimeHelper = ZonedTimeHelper(localToRomeDateTime(referenceDateTime))

        val params = "?type=${stopLineType.value}&refDateTime=${romeReferenceDateTime.toInstant()}"
        return JSONArray(httpClient.fetchJson(BASE_URL + TRIPS_PATH + params + otherParams))
            .map { it: JSONObject -> transform(it, zonedTimeHelper) }
    }

    /**
     * Get updates about the provided trip.
     *
     * @param tripId the id of the trip as returned in [getTripsByStop] or [getTripsByLine]
     * @param referenceDateTime used to produce the dates in the trips' [StopTime]s
     * @return the requested trip (obviously with the latest information)
     */
    fun getTripById(tripId: String, referenceDateTime: LocalDateTime): Trip {
        return tripFromJSONObject(
            JSONObject(httpClient.fetchJson(BASE_URL + TRIP_PATH + tripId)),
            ZonedTimeHelper(localToRomeDateTime(referenceDateTime))
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