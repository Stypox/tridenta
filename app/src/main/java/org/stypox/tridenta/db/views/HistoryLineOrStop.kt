package org.stypox.tridenta.db.views

import androidx.annotation.ColorInt
import androidx.room.DatabaseView
import org.stypox.tridenta.db.data.DbLine
import org.stypox.tridenta.db.data.DbStop
import org.stypox.tridenta.enums.Area
import org.stypox.tridenta.enums.CardinalPoint
import org.stypox.tridenta.enums.StopLineType
import java.time.OffsetDateTime

@DatabaseView(
    """
        SELECT

            H.*,
        
            S.latitude as stopLatitude, S.longitude AS stopLongitude,
            S.name as stopName, S.street as stopStreet, S.town as stopTown,
            S.wheelchairAccessible as stopWheelchairAccessible,
            S.cardinalPoint as stopCardinalPoint,
            
            L.area as lineArea, L.color as lineColor,
            L.longName as lineLongName, L.shortName as lineShortName
            
        FROM HistoryEntry AS H
        LEFT OUTER JOIN DbStop AS S
            ON S.stopId = H.id
            AND S.type = H.type
            AND H.isLine = 0
        LEFT OUTER JOIN DbLine AS L
            ON L.lineId = H.id
            AND L.type = H.type
            AND H.isLine = 1
    """
)
class HistoryLineOrStop(
    private val isLine: Boolean,
    private val id: Int,
    private val type: StopLineType,
    private val timesAccessed: Int,
    private val lastAccessed: OffsetDateTime,
    private val isFavorite: Boolean,
    private val stopLatitude: Double?,
    private val stopLongitude: Double?,
    private val stopName: String?,
    private val stopStreet: String?,
    private val stopTown: String?,
    private val stopWheelchairAccessible: Boolean?,
    private val stopCardinalPoint: CardinalPoint?,
    private val lineArea: Area?,
    @ColorInt private val lineColor: Int?,
    private val lineLongName: String?,
    private val lineShortName: String?,
) {
    /**
     * @return an object among these types:
     * - [DbLine] if this history entry is for a line
     * - [DbStop] if this history entry is for a stop
     * - `null` for unmatched history entries
     */
    fun intoLineOrStop(): Any? {
        if (isLine) {
            if (lineArea == null || lineLongName == null || lineShortName == null) {
                return null;
            }

            return DbLine(
                lineId = id,
                type = type,
                area = lineArea,
                color = lineColor,
                longName = lineLongName,
                shortName = lineShortName,
                isFavorite = isFavorite,
            )
        } else {
            if (stopLatitude == null || stopLongitude == null || stopName == null
                || stopStreet == null || stopTown == null || stopWheelchairAccessible == null
            ) {
                return null;
            }

            return DbStop(
                stopId = id,
                type = type,
                latitude = stopLatitude,
                longitude = stopLongitude,
                name = stopName,
                street = stopStreet,
                town = stopTown,
                wheelchairAccessible = stopWheelchairAccessible,
                cardinalPoint = stopCardinalPoint,
                isFavorite = isFavorite,
            )
        }
    }
}