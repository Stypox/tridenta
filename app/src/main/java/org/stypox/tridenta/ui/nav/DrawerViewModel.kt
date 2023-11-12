package org.stypox.tridenta.ui.nav

import android.app.Application
import android.content.Context
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import dagger.hilt.android.lifecycle.HiltViewModel
import org.stypox.tridenta.db.data.DbLine
import org.stypox.tridenta.db.data.DbStop
import org.stypox.tridenta.db.views.HistoryLineOrStop
import org.stypox.tridenta.repo.HistoryRepository
import org.stypox.tridenta.util.buildLineShortcutInfo
import org.stypox.tridenta.util.buildStopShortcutInfo
import javax.inject.Inject


/**
 * Also takes care of creating dynamic shortcuts.
 */
@HiltViewModel
class DrawerViewModel @Inject constructor(
    application: Application,
    historyRepository: HistoryRepository
) : AndroidViewModel(application) {

    val favorites = historyRepository.getFavorites()
    val history = historyRepository.getHistory(limit = MAX_HISTORY_ENTRIES_TO_SHOW)

    private val context: Context
        get() = getApplication<Application>().baseContext

    private var entriesForShortcuts: LiveData<List<HistoryLineOrStop>>? = null
    private var entriesForShortcutsObserver: Observer<List<HistoryLineOrStop>>? = null

    init {
        // note: this is not actually the number of items shown in the launcher's menu
        val dynamicShortcutCount = ShortcutManagerCompat.getMaxShortcutCountPerActivity(context) -
                STATIC_SHORTCUT_COUNT
        if (dynamicShortcutCount > 0) {

            entriesForShortcuts = historyRepository.getEntriesForShortcuts(
                limit = dynamicShortcutCount
            )

            entriesForShortcutsObserver = Observer { items ->
                ShortcutManagerCompat.removeAllDynamicShortcuts(context)
                items.forEachIndexed { index, historyEntry ->
                    val shortcutInfo = when (val item = historyEntry.intoLineOrStop()) {
                        is DbStop -> buildStopShortcutInfo(context, item)
                        is DbLine -> buildLineShortcutInfo(context, item)
                        else -> return@forEachIndexed
                    }
                        .setRank(index)

                    ShortcutManagerCompat.pushDynamicShortcut(context, shortcutInfo.build())
                }
            }

            entriesForShortcuts!!.observeForever(entriesForShortcutsObserver!!)
        }
    }

    override fun onCleared() {
        super.onCleared()
        entriesForShortcutsObserver?.let {
            entriesForShortcuts?.removeObserver(it)
        }
    }

    companion object {
        const val MAX_HISTORY_ENTRIES_TO_SHOW = 16
        const val STATIC_SHORTCUT_COUNT = 2
    }
}