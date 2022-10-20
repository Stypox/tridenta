package org.stypox.tridenta.db.data

import androidx.annotation.ColorInt
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Ignore
import androidx.room.Index
import org.stypox.tridenta.enums.Area
import org.stypox.tridenta.enums.StopLineType
import java.time.OffsetDateTime

@Entity(
    primaryKeys = ["lineId", "type"]
)
data class DbLine(
    // some testing exposed that a line is always identified by the (lineId, type) tuple
    val lineId: Int,
    val type: StopLineType,
    val area: Area,
    @ColorInt val color: Int?,
    val longName: String,
    val shortName: String,
    @Ignore val isFavorite: Boolean,
) {
    // needed for Room (@JvmOverloads does not work)
    constructor(
        lineId: Int,
        type: StopLineType,
        area: Area,
        @ColorInt color: Int?,
        longName: String,
        shortName: String,
    ) : this(lineId, type, area, color, longName, shortName, false)
}

@Entity(
    // everything is a primary key since there is no unique key provided by the server
    primaryKeys = ["serviceType", "startDate", "endDate", "header",
        "details", "url", "lineId", "lineType"],
    foreignKeys = [
        ForeignKey(
            entity = DbLine::class,
            parentColumns = ["lineId", "type"],
            childColumns = ["lineId", "lineType"],
            onDelete = CASCADE
        )
    ],
    indices = [
        Index("lineId", "lineType")
    ]
)
data class DbNewsItem(
    val serviceType: String,
    val startDate: OffsetDateTime,
    val endDate: OffsetDateTime,
    val header: String,
    val details: String,
    val url: String,
    // this is the foreign key to a line
    val lineId: Int,
    val lineType: StopLineType,
)