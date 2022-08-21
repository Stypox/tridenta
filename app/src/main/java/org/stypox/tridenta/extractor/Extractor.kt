package org.stypox.tridenta.extractor

import org.json.JSONArray
import org.json.JSONObject
import org.stypox.tridenta.data.*
import java.time.LocalDateTime
import javax.inject.Inject

class Extractor @Inject constructor(private val httpClient: HttpClient) {

    /**
     * Get all existing stops. The response's size will be ~2MB, so persist the returned data!
     *
     * @param limit return only this number of results. A negative value means "no limitation".
     *              Providing this parameter reduces the response size, though this is useful only
     *              for testing purposes, as otherwise it does not make sense to fetch only some of
     *              the stops at random.
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
     */
    fun getLines(areas: Array<Area> = Area.values()): List<Line> {
        val params = "?areas=" + areas.map { it.value }.joinToString(",")
        return JSONArray(httpClient.fetchJson(BASE_URL + LINES_PATH + params))
            .map(::lineFromJSONObject)
    }

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