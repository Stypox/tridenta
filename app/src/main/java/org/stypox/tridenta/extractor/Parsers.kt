package org.stypox.tridenta.extractor

import androidx.annotation.ColorInt
import org.json.JSONObject
import org.stypox.tridenta.enums.*
import org.stypox.tridenta.extractor.data.*


fun stopFromJSONObject(o: JSONObject): ExStop {
    return ExStop(
        stopId = o.getInt("stopId"),
        latitude = o.getDouble("stopLat"),
        longitude = o.getDouble("stopLon"),
        name = o.getString("stopName"),
        street = o.optString("street", ""),
        town = o.optString("town", ""),
        type = stopLineTypeFromString(o.getString("type")),
        wheelchairAccessible = o.optInt("wheelchairBoarding") == 1,
        lines = o.getJSONArray("routes").map(::lineFromJSONObject)
    )
}

fun lineFromJSONObject(o: JSONObject): ExLine {
    val newsItems = if (o.isNull("news"))
        listOf()
    else
        o.getJSONArray("news").map(::newsItemFromJSONObject)

    return ExLine(
        lineId = o.getInt("routeId"),
        area = areaFromInt(o.getInt("areaId")),
        color = colorFromString(o.optString("routeColor")),
        longName = o.getString("routeLongName"),
        shortName = o.getString("routeShortName"),
        type = stopLineTypeFromString(o.getString("type")),
        newsItems = newsItems
    )
}

fun newsItemFromJSONObject(o: JSONObject): ExNewsItem {
    return ExNewsItem(
        serviceType = o.getString("serviceType"),
        startDate = dateTimeFromEpochString(o.getString("startDate")),
        endDate = dateTimeFromEpochString(o.getString("endDate")),
        header = o.getString("header"),
        details = o.getString("details"),
        url = o.getString("url"),
        // o.getJSONArray("routeIds") would contain the affected line ids, but it's redundant
    )
}

fun tripWithIndexFromJSONObject(
    o: JSONObject,
    zonedTimeHelper: ZonedTimeHelper
) : Pair<Int, ExTrip> {
    return Pair(o.getInt("indiceCorsaInLista"), tripFromJSONObject(o, zonedTimeHelper))
}

fun tripFromJSONObject(o: JSONObject, zonedTimeHelper: ZonedTimeHelper): ExTrip {
    val stopTimes = o.getJSONArray("stopTimes")
        .map { it: JSONObject -> it }
        // make sure they are sorted by the stop index
        .sortedBy { it.getInt("stopSequence") }
        .map { stopTimeFromJsonObject(it, zonedTimeHelper) }
    return ExTrip(
        delay = o.optInt("delay", ExTrip.DELAY_UNKNOWN),
        direction = directionFromInt(o.getInt("directionId")),
        lastEventReceivedAt = dateTimeFromISOString(o.optString("lastEventRecivedAt")),
        lineId = o.getInt("routeId"),
        headSign = o.getString("tripHeadsign"),
        tripId = o.getString("tripId"),
        type = stopLineTypeFromString(o.getString("type")),
        completedStops = stopTimes.indexOfFirst { it.stopId == o.getInt("stopLast") },
        stopTimes = stopTimes
    )
}

fun stopTimeFromJsonObject(
    o: JSONObject,
    zonedTimeHelper: ZonedTimeHelper
): ExStopTime {
    return ExStopTime(
        arrivalTime = zonedTimeHelper.timeFromRomeString(o.getString("arrivalTime")),
        departureTime = zonedTimeHelper.timeFromRomeString(o.getString("departureTime")),
        stopId = o.getInt("stopId"),
    )
}

fun stopLineTypeFromString(s: String): StopLineType {
    return StopLineType.values().first { type -> type.value == s }
}

fun areaFromInt(i: Int): Area? {
    // TODO why can i be 0? It does not make sense to have null as the area
    return if (i == 0) null else Area.values().first { area -> area.value == i }
}

fun directionFromInt(i: Int): Direction {
    return Direction.values().first { direction -> direction.value == i }
}

@ColorInt
fun colorFromString(s: String?): Int? {
    return if (s.isNullOrEmpty() || s == "null") null else s.toInt(16)
}