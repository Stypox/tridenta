package org.stypox.tridenta.ui.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.stypox.tridenta.db.LogDao
import javax.inject.Inject

@HiltViewModel
class LogsViewModel @Inject constructor(private val logDao: LogDao) : ViewModel() {
    val logs = logDao.getLogs()

    fun clearLogs() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                logDao.clearLogs()
            }
        }
    }
}