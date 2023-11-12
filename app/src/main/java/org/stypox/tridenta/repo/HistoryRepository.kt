package org.stypox.tridenta.repo

import androidx.lifecycle.LiveData
import org.stypox.tridenta.db.HistoryDao
import org.stypox.tridenta.db.views.HistoryLineOrStop
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    private val historyDao: HistoryDao,
) {
    fun getFavorites(): LiveData<List<HistoryLineOrStop>> {
        // no limit or offset here, we want to show all favorites directly
        return historyDao.getFavoritesSortedByTimesAccessed()
    }

    fun getHistory(limit: Int): LiveData<List<HistoryLineOrStop>> {
        return historyDao.getHistorySortedByLastAccessed(limit = limit)
    }

    fun getEntriesForShortcuts(limit: Int): LiveData<List<HistoryLineOrStop>> {
        return historyDao.getEntriesForShortcuts(limit = limit)
    }
}