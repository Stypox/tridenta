package org.stypox.tridenta.db.data

import androidx.room.Entity
import org.stypox.tridenta.enums.StopLineType
import java.time.OffsetDateTime

@Entity(
    primaryKeys = ["isLine", "id", "type"]
)
data class HistoryEntry(
    val isLine: Boolean, // if true, this is a line, otherwise it is a stop
    val id: Int,
    val type: StopLineType,
    val timesAccessed: Int,
    val lastAccessed: OffsetDateTime,
    val isFavorite: Boolean,
)