package org.stypox.tridenta.repo.data

import org.stypox.tridenta.db.data.DbLine
import org.stypox.tridenta.db.data.DbStop
import org.stypox.tridenta.enums.StopLineType

data class UiStop(
    // some testing exposed that a stop is always identified by the (stopId, type) tuple
    val stopId: Int,
    val type: StopLineType,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val street: String,
    val town: String,
    val wheelchairAccessible: Boolean,
    val lines: List<DbLine>,
    val isFavorite: Boolean,
) {
    constructor(dbStop: DbStop, lines: List<DbLine>) : this(
        stopId = dbStop.stopId,
        type = dbStop.type,
        latitude = dbStop.latitude,
        longitude = dbStop.longitude,
        name = dbStop.name,
        street = dbStop.street,
        town = dbStop.town,
        wheelchairAccessible = dbStop.wheelchairAccessible,
        lines = lines,
        isFavorite = dbStop.isFavorite,
    )
}
