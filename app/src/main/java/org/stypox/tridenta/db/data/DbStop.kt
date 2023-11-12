package org.stypox.tridenta.db.data

import androidx.room.*
import org.stypox.tridenta.enums.CardinalPoint
import org.stypox.tridenta.enums.StopLineType

@Entity(
    primaryKeys = ["stopId", "type"]
)
data class DbStop(
    // some testing exposed that a stop is always identified by the (stopId, type) tuple
    val stopId: Int,
    val type: StopLineType,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val street: String,
    val town: String,
    val wheelchairAccessible: Boolean,
    val cardinalPoint: CardinalPoint?,
    @Ignore val isFavorite: Boolean,
) {
    constructor(
        stopId: Int,
        type: StopLineType,
        latitude: Double,
        longitude: Double,
        name: String,
        street: String,
        town: String,
        wheelchairAccessible: Boolean,
        cardinalPoint: CardinalPoint?,
    ) : this(stopId, type, latitude, longitude, name, street, town, wheelchairAccessible, cardinalPoint, false)
}

@Entity(
    primaryKeys = ["stopId", "stopType", "lineId", "lineType"],
    foreignKeys = [
        ForeignKey(
            entity = DbStop::class,
            parentColumns = ["stopId", "type"],
            childColumns = ["stopId", "stopType"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = DbLine::class,
            parentColumns = ["lineId", "type"],
            childColumns = ["lineId", "lineType"],
            onDelete = ForeignKey.CASCADE,
            // when lines are reloaded, they are all deleted and reinserted, but we don't want to
            // lose the stop-line join data, since such data is reloaded
            deferred = true,
        )
    ],
    indices = [
        Index("stopId", "stopType"),
        Index("lineId", "lineType")
    ]
)
data class DbStopLineJoin(
    val stopId: Int,
    val stopType: StopLineType,
    val lineId: Int,
    val lineType: StopLineType,
)
