package org.stypox.tridenta.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
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
     * @return a list with elements among these types:
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
     * @return a list with elements among these types:
     * - [org.stypox.tridenta.db.data.DbLine] for history lines
     * - [org.stypox.tridenta.db.data.DbStop] for history stops
     * - [org.stypox.tridenta.db.data.HistoryEntry] for unmatched entries
     */
    fun getHistory(coroutineScope: CoroutineScope): LiveData<List<Any>> {
        // TODO implement limit and offset
        return historyEntriesToObjects(
            coroutineScope,
            historyDao.getHistorySortedByLastAccessed(limit = 10, offset = 0)
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