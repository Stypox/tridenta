package org.stypox.tridenta.db.views

import androidx.room.DatabaseView
import androidx.room.Embedded
import org.stypox.tridenta.db.data.DbStop

@DatabaseView(
    """
            SELECT DbStop.*,
                HistoryEntry.isFavorite <> 0 AS isFavorite
            FROM DbStop LEFT OUTER JOIN HistoryEntry
                ON HistoryEntry.isLine = 0
                    AND DbStop.stopId = HistoryEntry.id
                    AND DbStop.type = HistoryEntry.type
        """
)
data class DbStopAndFavorite(
    @Embedded private val dbStopWithoutFavorite: DbStop,
    private val isFavorite: Boolean
) {
    // Room does not support better ways to do fill in @Ignored fields, unfortunately
    val dbStop: DbStop get() = dbStopWithoutFavorite.copy(isFavorite = isFavorite)
}