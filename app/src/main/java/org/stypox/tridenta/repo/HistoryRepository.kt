package org.stypox.tridenta.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import org.stypox.tridenta.db.HistoryDao
import org.stypox.tridenta.db.LineDao
import org.stypox.tridenta.db.StopDao
import org.stypox.tridenta.db.data.HistoryEntry
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    private val historyDao: HistoryDao,
    private val stopDao: StopDao,
    private val lineDao: LineDao
) {
    /**
     * @return a list with elements among these types:
     * - [org.stypox.tridenta.db.data.DbLine] for favorite lines
     * - [org.stypox.tridenta.db.data.DbStop] for favorite stops
     * - [org.stypox.tridenta.db.data.HistoryEntry] for unmatched entries
     */
    fun getFavorites(): LiveData<List<Any>> {
        // no limit or offset here, we want to show all favorites directly
        return historyEntriesToObjects(historyDao.getFavoritesSortedByTimesAccessed())
    }

    /**
     * @return a list with elements among these types:
     * - [org.stypox.tridenta.db.data.DbLine] for history lines
     * - [org.stypox.tridenta.db.data.DbStop] for history stops
     * - [org.stypox.tridenta.db.data.HistoryEntry] for unmatched entries
     */
    fun getHistory(): LiveData<List<Any>> {
        // TODO implement limit and offset
        return historyEntriesToObjects(
            historyDao.getHistorySortedByLastAccessed(limit = 10, offset = 0)
        )
    }

    private fun historyEntriesToObjects(
        liveHistoryEntries: LiveData<List<HistoryEntry>>
    ): LiveData<List<Any>>{
        return Transformations.map(liveHistoryEntries) { historyEntries ->
            historyEntries.map { historyEntry ->
                if (historyEntry.isLine) {
                    lineDao.getLine(historyEntry.id, historyEntry.type)
                } else {
                    stopDao.getStop(historyEntry.id, historyEntry.type)
                } ?: historyEntry
            }
        }
    }
}