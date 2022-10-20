package org.stypox.tridenta.db.views

import androidx.room.DatabaseView
import androidx.room.Embedded
import org.stypox.tridenta.db.data.DbLine

@DatabaseView(
    """
            SELECT DbLine.*,
                HistoryEntry.isFavorite <> 0 AS isFavorite
            FROM DbLine LEFT OUTER JOIN HistoryEntry
                ON HistoryEntry.isLine <> 0
                    AND DbLine.lineId = HistoryEntry.id
                    AND DbLine.type = HistoryEntry.type
        """
)
data class DbLineAndFavorite(
    @Embedded private val dbLineWithoutFavorite: DbLine,
    private val isFavorite: Boolean
) {
    // Room does not support better ways to do fill in @Ignored fields, unfortunately
    val dbLine: DbLine get() = dbLineWithoutFavorite.copy(isFavorite = isFavorite)
}