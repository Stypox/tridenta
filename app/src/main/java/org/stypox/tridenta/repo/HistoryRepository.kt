package org.stypox.tridenta.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
     * @return a list of favorite elements among these types:
     * - [org.stypox.tridenta.db.data.DbLine] for favorite lines
     * - [org.stypox.tridenta.db.data.DbStop] for favorite stops
     * - [org.stypox.tridenta.db.data.HistoryEntry] for unmatched entries
     */
    fun getFavorites(coroutineScope: CoroutineScope): LiveData<List<Any>> {
        // no limit or offset here, we want to show all favorites directly
        return historyEntriesToObjects(
            coroutineScope,
            historyDao.getFavoritesSortedByTimesAccessed()
        )
    }

    /**
     * @return a list of only the latest few history elements among these types:
     * - [org.stypox.tridenta.db.data.DbLine] for history lines
     * - [org.stypox.tridenta.db.data.DbStop] for history stops
     * - [org.stypox.tridenta.db.data.HistoryEntry] for unmatched entries
     */
    fun getHistory(coroutineScope: CoroutineScope, limit: Int): LiveData<List<Any>> {
        return historyEntriesToObjects(
            coroutineScope,
            historyDao.getHistorySortedByLastAccessed(limit = limit)
        )
    }

    /**
     * @return a list of favorite and history elements among these types:
     * - [org.stypox.tridenta.db.data.DbLine] for history lines
     * - [org.stypox.tridenta.db.data.DbStop] for history stops
     * - [org.stypox.tridenta.db.data.HistoryEntry] for unmatched entries
     */
    fun getEntriesForShortcuts(coroutineScope: CoroutineScope, limit: Int): LiveData<List<Any>> {
        return historyEntriesToObjects(
            coroutineScope,
            historyDao.getEntriesForShortcuts(limit = limit)
        )
    }

    private fun historyEntriesToObjects(
        coroutineScope: CoroutineScope,
        liveHistoryEntries: LiveData<List<HistoryEntry>>
    ): LiveData<List<Any>>{
        return MediatorLiveData<List<Any>>().apply {
            addSource(liveHistoryEntries) { historyEntries ->
                coroutineScope.launch {
                    value = withContext(Dispatchers.IO) {
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
        }
    }
}