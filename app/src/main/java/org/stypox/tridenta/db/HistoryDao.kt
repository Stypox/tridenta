package org.stypox.tridenta.db

import androidx.lifecycle.LiveData
import androidx.room.*
import org.stypox.tridenta.db.data.HistoryEntry
import org.stypox.tridenta.enums.StopLineType
import java.time.OffsetDateTime

@Dao
interface HistoryDao {

    @Query(
        """
            SELECT *
            FROM HistoryEntry
            WHERE HistoryEntry.isLine = :isLine
                AND HistoryEntry.id = :id
                AND HistoryEntry.type = :type
        """
    )
    fun getHistoryEntryOrNull(isLine: Boolean, id: Int, type: StopLineType): HistoryEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertHistoryEntry(historyEntry: HistoryEntry)

    @Query(
        """
            SELECT EXISTS (
                SELECT *
                FROM HistoryEntry
                WHERE HistoryEntry.isLine = :isLine
                    AND HistoryEntry.id = :id
                    AND HistoryEntry.type = :type
                    AND HistoryEntry.isFavorite <> 0
            )
        """
    )
    fun isFavorite(isLine: Boolean, id: Int, type: StopLineType): LiveData<Boolean>

    @Query(
        """
            SELECT *
            FROM HistoryEntry
            WHERE HistoryEntry.isFavorite <> 0
            ORDER BY HistoryEntry.timesAccessed DESC
        """
    )
    fun getFavoritesSortedByTimesAccessed(): LiveData<List<HistoryEntry>>

    /**
     * Does not include favorite items, since those are already shown in another part of the ui and
     * it wouldn't make sense to show them twice.
     */
    @Query(
        """
            SELECT *
            FROM HistoryEntry
            WHERE HistoryEntry.isFavorite = 0
            ORDER BY HistoryEntry.lastAccessed DESC
            LIMIT :limit OFFSET :offset
        """
    )
    fun getHistorySortedByLastAccessed(limit: Int, offset: Int): LiveData<List<HistoryEntry>>

    @Query(
        """
            SELECT *
            FROM HistoryEntry
            ORDER BY (HistoryEntry.isFavorite <> 0) DESC, HistoryEntry.lastAccessed DESC
            LIMIT :limit OFFSET 0
        """
    )
    fun getEntriesForShortcuts(limit: Int): LiveData<List<HistoryEntry>>

    @Transaction
    fun registerAccessed(isLine: Boolean, id: Int, type: StopLineType) {
        val historyEntry = getHistoryEntryOrNull(isLine, id, type)
            ?.let {
                // just update, so leave all other fields unchanged
                it.copy(
                    timesAccessed = it.timesAccessed + 1,
                    lastAccessed = OffsetDateTime.now()
                )
            }
            // create new entry
            ?: HistoryEntry(
                isLine = isLine,
                id = id,
                type = type,
                timesAccessed = 1,
                lastAccessed = OffsetDateTime.now(),
                isFavorite = false
            )

        upsertHistoryEntry(historyEntry)
    }

    @Transaction
    fun setFavorite(isLine: Boolean, id: Int, type: StopLineType, isFavorite: Boolean) {
        val prevHistoryEntry = getHistoryEntryOrNull(isLine, id, type)
        if (isFavorite == (prevHistoryEntry != null && prevHistoryEntry.isFavorite)) {
            return // no need to create or update an entry if nothing would change anyway
        }

        val historyEntry = prevHistoryEntry
            ?.copy(isFavorite = isFavorite)
            ?: HistoryEntry(
                isLine = isLine,
                id = id,
                type = type,
                timesAccessed = 0,
                lastAccessed = OffsetDateTime.now(),
                isFavorite = isFavorite
            )

        upsertHistoryEntry(historyEntry)
    }
}