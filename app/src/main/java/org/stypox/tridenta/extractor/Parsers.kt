package org.stypox.tridenta.extractor

import androidx.annotation.ColorInt
import org.json.JSONObject
import org.stypox.tridenta.data.*


fun stopFromJSONObject(o: JSONObject): Stop {
    val address = listOfNotNull(o.get("street"), o.get("town")).joinToString(" - ")
    return Stop(
        stopId = o.getInt("stopId"),
        latitude = o.getDouble("stopLat"),
        longitude = o.getDouble("stopLon"),
        name = o.getString("stopName"),
        address = address,
        type = stopLineTypeFromString(o.getString("type")),
        lines = o.getJSONArray("routes").map(::lineFromJSONObject)
    )
}

fun lineFromJSONObject(o: JSONObject): Line {
    val newsItems = if (o.isNull("news"))
        listOf()
    else
        o.getJSONArray("news").map(::newsItemFromJSONObject)

    return Line(
        lineId = o.getInt("routeId"),
        area = areaFromInt(o.getInt("areaId")),
        color = colorFromString(o.optString("routeColor")),
        longName = o.getString("routeLongName"),
        shortName = o.getString("routeShortName"),
        type = stopLineTypeFromString(o.getString("type")),
        newsItems = newsItems
    )
}

fun newsItemFromJSONObject(o: JSONObject): NewsItem {
    return NewsItem(
        serviceType = o.getString("serviceType"),
        startDate = dateTimeFromEpochString(o.getString("startDate")),
        endDate = dateTimeFromEpochString(o.getString("endDate")),
        header = o.getString("header"),
        details = o.getString("details"),
        url = o.getString("url"),
        affectedLineIds = o.getJSONArray("routeIds").map { i: Int -> i }
    )
}

fun tripWithIndexFromJSONObject(
    o: JSONObject,
    zonedTimeHelper: ZonedTimeHelper
) : Pair<Int, Trip> {
    return Pair(o.getInt("indiceCorsaInLista"), tripFromJSONObject(o, zonedTimeHelper))
}

fun tripFromJSONObject(o: JSONObject, zonedTimeHelper: ZonedTimeHelper): Trip {
    return Trip(
        delay = o.optInt("delay", Trip.DELAY_UNKNOWN),
        direction = directionFromInt(o.getInt("directionId")),
        lastEventReceivedAt = dateTimeFromISOString(o.optString("lastEventRecivedAt")),
        lineId = o.getInt("routeId"),
        headSign = o.getString("tripHeadsign"),
        tripId = o.getString("tripId"),
        type = stopLineTypeFromString(o.getString("type")),
        completedStops = o.getInt("stopLast"),
        stopTimes = o.getJSONArray("stopTimes")
            .map { it: JSONObject -> it }
            // make sure they are sorted by the stop index
            .sortedBy { it.getInt("stopSequence") }
            .map { stopTimeFromJsonObject(it, zonedTimeHelper) }
    )
}

fun stopTimeFromJsonObject(
    o: JSONObject,
    zonedTimeHelper: ZonedTimeHelper
): StopTime {
    return StopTime(
        arrivalTime = zonedTimeHelper.timeFromRomeString(o.getString("arrivalTime")),
        departureTime = zonedTimeHelper.timeFromRomeString(o.getString("departureTime")),
        stopId = o.getInt("stopId"),
    )
}

fun stopLineTypeFromString(s: String): StopLineType {
    return StopLineType.values().first { type -> type.value == s }
}

fun areaFromInt(i: Int): Area? {
    return if (i == 0) null else Area.values().first { area -> area.value == i }
}

fun directionFromInt(i: Int): Direction {
    return Direction.values().first { direction -> direction.value == i }
}

@ColorInt
fun colorFromString(s: String?): Int? {
    return if (s.isNullOrEmpty()) null else s.toInt(16)
}